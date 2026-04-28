import { LitElement, html, css, PropertyValues } from "lit";
import { property, query } from "lit/decorators.js";

export abstract class PixelChartBase extends LitElement {
  static styles = css`
    :host {
      display: block;
      width: 100%;
      height: 200px;
    }
    canvas {
      width: 100%;
      height: 100%;
      display: block;
    }
  `;

  @property({ type: Number }) pixelSize = 8;
  @property({ type: Number }) gap = 2;
  @property({ type: Number }) glowIntensity = 0.5;
  @property({ type: Number }) pixelRadius = 0;
  @property({ type: Number }) highlightRadius = 3;
  @property({ type: Boolean }) highlightEnabled = true;
  @property({ type: Array }) colors: string[] = [];
  @property({ type: Array }) items: any[] = [];

  // Hover state (canvas CSS pixels, -1 when not hovering)
  private _hoverPx = -1;
  private _hoverPy = -1;

  @query("canvas") private canvas!: HTMLCanvasElement;

  private _resizeObserver?: ResizeObserver;
  private _mutationObserver?: MutationObserver;
  private _pendingFrame = false;
  private _boundMouseMove = this._onMouseMove.bind(this);
  private _boundMouseLeave = this._onMouseLeave.bind(this);
  private _boundClick = this._onClick.bind(this);

  render() {
    return html`<canvas></canvas>`;
  }

  connectedCallback() {
    super.connectedCallback();

    this._resizeObserver = new ResizeObserver(() => this.requestRedraw());
    this._resizeObserver.observe(this);

    // Watch for theme changes on <html>
    this._mutationObserver = new MutationObserver(() => this.requestRedraw());
    this._mutationObserver.observe(document.documentElement, {
      attributes: true,
      attributeFilter: ["theme", "data-palette", "data-density", "class"],
    });
  }

  protected firstUpdated(_: PropertyValues) {
    super.firstUpdated(_);
    const c = this.canvas;
    if (c) {
      c.addEventListener("mousemove", this._boundMouseMove);
      c.addEventListener("mouseleave", this._boundMouseLeave);
      c.addEventListener("click", this._boundClick);
    }
  }

  disconnectedCallback() {
    super.disconnectedCallback();
    this._resizeObserver?.disconnect();
    this._mutationObserver?.disconnect();
    const c = this.canvas;
    if (c) {
      c.removeEventListener("mousemove", this._boundMouseMove);
      c.removeEventListener("mouseleave", this._boundMouseLeave);
      c.removeEventListener("click", this._boundClick);
    }
  }

  private _canvasXY(e: MouseEvent): { x: number; y: number } {
    const rect = this.canvas.getBoundingClientRect();
    return { x: e.clientX - rect.left, y: e.clientY - rect.top };
  }

  private _onMouseMove(e: MouseEvent) {
    const pos = this._canvasXY(e);
    this._hoverPx = pos.x;
    this._hoverPy = pos.y;
    this.requestRedraw();
    const hit = this.hitTest(pos.x, pos.y, this.clientWidth, this.clientHeight);
    this.dispatchEvent(new CustomEvent("pixel-hover", {
      detail: { ...pos, ...hit },
      bubbles: true,
      composed: true,
    }));
  }

  private _onMouseLeave(_: MouseEvent) {
    this._hoverPx = -1;
    this._hoverPy = -1;
    this.requestRedraw();
    this.dispatchEvent(new CustomEvent("pixel-hover", {
      detail: { x: -1, y: -1, index: -1, value: null, label: null },
      bubbles: true,
      composed: true,
    }));
  }

  private _onClick(e: MouseEvent) {
    const pos = this._canvasXY(e);
    const hit = this.hitTest(pos.x, pos.y, this.clientWidth, this.clientHeight);
    this.dispatchEvent(new CustomEvent("pixel-click", {
      detail: { ...pos, ...hit },
      bubbles: true,
      composed: true,
    }));
  }

  /** Override in subclasses to resolve what data point is at (px, py). */
  protected hitTest(
    px: number,
    py: number,
    width: number,
    height: number
  ): { index: number; value: number | null; label: string | null; row?: number; col?: number } {
    return { index: -1, value: null, label: null };
  }

  protected updated(_changedProperties: PropertyValues) {
    super.updated(_changedProperties);
    this.requestRedraw();
  }

  protected requestRedraw() {
    if (this._pendingFrame) return;
    this._pendingFrame = true;
    requestAnimationFrame(() => {
      this._pendingFrame = false;
      this._draw();
    });
  }

  private _draw() {
    const canvas = this.canvas;
    if (!canvas) return;

    const dpr = window.devicePixelRatio || 1;
    const w = this.clientWidth;
    const h = this.clientHeight;
    if (w === 0 || h === 0) return;

    canvas.width = w * dpr;
    canvas.height = h * dpr;

    const ctx = canvas.getContext("2d")!;
    ctx.setTransform(dpr, 0, 0, dpr, 0, 0);
    ctx.clearRect(0, 0, w, h);

    this.renderChart(ctx, w, h);

    if (this.highlightEnabled && this._hoverPx >= 0) {
      this._renderHighlight(ctx, w, h);
    }
  }

  protected abstract renderChart(
    ctx: CanvasRenderingContext2D,
    width: number,
    height: number
  ): void;

  /**
   * Hover highlight overlay — CRT crosshair + radial glow.
   * Drawn after renderChart() using additive blending.
   */
  private _renderHighlight(ctx: CanvasRenderingContext2D, width: number, height: number) {
    const step = this.step;
    const ps = this.pixelSize;
    const hCol = Math.floor(this._hoverPx / step);
    const hRow = Math.floor(this._hoverPy / step);
    const maxCols = Math.floor(width / step);
    const maxRows = Math.floor(height / step);
    const radius = this.highlightRadius;

    // Resolve highlight color (accent from theme)
    const cs = getComputedStyle(this);
    const accent =
      cs.getPropertyValue("--pixel-chart-highlight-color").trim() ||
      cs.getPropertyValue("--lumo-primary-color").trim() ||
      "#ffffff";

    const r = Math.min(this.pixelRadius, ps / 2);

    // Use 'lighter' composite for additive glow on existing pixels,
    // then 'source-over' for crosshair tint on empty areas
    const saved = ctx.globalCompositeOperation;

    // --- Pass 1: crosshair lines (fixed medium intensity) ---
    ctx.globalCompositeOperation = "source-over";
    ctx.shadowBlur = 0;
    ctx.shadowColor = "transparent";
    const crossAlpha = 0.08;

    // Full column
    for (let row = 0; row < maxRows; row++) {
      if (row === hRow) continue; // skip center, handled in radial
      const dist = Math.abs(row - hRow);
      if (dist <= radius) continue; // handled in radial
      ctx.fillStyle = accent;
      ctx.globalAlpha = crossAlpha;
      this._fillPixel(ctx, hCol * step, row * step, ps, r);
    }

    // Full row
    for (let col = 0; col < maxCols; col++) {
      if (col === hCol) continue;
      const dist = Math.abs(col - hCol);
      if (dist <= radius) continue;
      ctx.fillStyle = accent;
      ctx.globalAlpha = crossAlpha;
      this._fillPixel(ctx, col * step, hRow * step, ps, r);
    }

    // --- Pass 2: radial glow (additive, decreasing intensity) ---
    ctx.globalCompositeOperation = "lighter";
    for (let dr = -radius; dr <= radius; dr++) {
      for (let dc = -radius; dc <= radius; dc++) {
        const col = hCol + dc;
        const row = hRow + dr;
        if (col < 0 || col >= maxCols || row < 0 || row >= maxRows) continue;

        const dist = Math.max(Math.abs(dr), Math.abs(dc)); // Chebyshev
        const intensity = 1.0 - dist / (radius + 1);

        // Center pixel: strongest
        if (dr === 0 && dc === 0) {
          ctx.globalAlpha = 0.35;
          ctx.shadowBlur = ps * 2;
          ctx.shadowColor = accent;
        } else {
          ctx.globalAlpha = intensity * 0.2;
          ctx.shadowBlur = ps * intensity;
          ctx.shadowColor = accent;
        }

        ctx.fillStyle = accent;
        this._fillPixel(ctx, col * step, row * step, ps, r);
      }
    }

    // --- Pass 3: crosshair within radius (lighter blend, medium) ---
    for (let row = hRow - radius; row <= hRow + radius; row++) {
      if (row < 0 || row >= maxRows) continue;
      const dist = Math.abs(row - hRow);
      if (dist === 0) continue;
      ctx.globalAlpha = 0.06;
      ctx.shadowBlur = 0;
      ctx.fillStyle = accent;
      this._fillPixel(ctx, hCol * step, row * step, ps, r);
    }
    for (let col = hCol - radius; col <= hCol + radius; col++) {
      if (col < 0 || col >= maxCols) continue;
      const dist = Math.abs(col - hCol);
      if (dist === 0) continue;
      ctx.globalAlpha = 0.06;
      ctx.shadowBlur = 0;
      ctx.fillStyle = accent;
      this._fillPixel(ctx, col * step, hRow * step, ps, r);
    }

    // Restore
    ctx.globalAlpha = 1.0;
    ctx.globalCompositeOperation = saved;
    ctx.shadowBlur = 0;
    ctx.shadowColor = "transparent";
  }

  /** Fill a pixel-sized rect, respecting corner radius */
  private _fillPixel(ctx: CanvasRenderingContext2D, x: number, y: number, size: number, radius: number) {
    if (radius > 0) {
      ctx.beginPath();
      ctx.roundRect(x, y, size, size, radius);
      ctx.fill();
    } else {
      ctx.fillRect(x, y, size, size);
    }
  }

  /** Resolve colors: custom colors → CSS variables → Lumo fallbacks */
  protected resolveColors(): string[] {
    if (this.colors.length > 0) return this.colors;

    const cs = getComputedStyle(this);

    // Try custom chart variables
    const custom: string[] = [];
    for (let i = 1; i <= 5; i++) {
      const v = cs.getPropertyValue(`--pixel-chart-color-${i}`).trim();
      if (v) custom.push(v);
    }
    if (custom.length > 0) return custom;

    // Fall back to Lumo
    return [
      cs.getPropertyValue("--lumo-primary-color").trim() || "#1676f3",
      cs.getPropertyValue("--lumo-success-color").trim() || "#1db954",
      cs.getPropertyValue("--lumo-error-color").trim() || "#e74c3c",
      cs.getPropertyValue("--lumo-warning-color").trim() || "#f39c12",
      cs.getPropertyValue("--lumo-primary-text-color").trim() || "#1676f3",
    ];
  }

  /** Resolve background color for clearing */
  protected resolveBackground(): string {
    const cs = getComputedStyle(this);
    return (
      cs.getPropertyValue("--pixel-chart-background").trim() ||
      cs.getPropertyValue("--lumo-contrast-5pct").trim() ||
      "transparent"
    );
  }

  /** Draw a single pixel block with optional glow and corner radius */
  protected drawPixelBlock(
    ctx: CanvasRenderingContext2D,
    x: number,
    y: number,
    color: string,
    glow: boolean = true
  ) {
    if (glow && this.glowIntensity > 0) {
      ctx.shadowBlur = this.pixelSize * this.glowIntensity * 1.5;
      ctx.shadowColor = color;
    } else {
      ctx.shadowBlur = 0;
      ctx.shadowColor = "transparent";
    }
    ctx.fillStyle = color;

    const r = Math.min(this.pixelRadius, this.pixelSize / 2);
    if (r > 0) {
      ctx.beginPath();
      ctx.roundRect(x, y, this.pixelSize, this.pixelSize, r);
      ctx.fill();
    } else {
      ctx.fillRect(x, y, this.pixelSize, this.pixelSize);
    }
  }

  /** Clear glow state */
  protected clearGlow(ctx: CanvasRenderingContext2D) {
    ctx.shadowBlur = 0;
    ctx.shadowColor = "transparent";
  }

  /** The step size (pixel + gap) */
  protected get step(): number {
    return this.pixelSize + this.gap;
  }
}
