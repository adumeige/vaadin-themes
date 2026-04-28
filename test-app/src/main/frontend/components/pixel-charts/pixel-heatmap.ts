import { customElement, property } from "lit/decorators.js";
import { PixelChartBase } from "./pixel-chart-base";

@customElement("pixel-heatmap")
export class PixelHeatmap extends PixelChartBase {
  @property({ type: Number }) rows = 7;
  @property({ type: Number }) cols = 24;

  protected renderChart(
    ctx: CanvasRenderingContext2D,
    width: number,
    height: number
  ) {
    const data = this.items as Array<{ row: number; col: number; value: number }>;
    if (!data || data.length === 0) return;

    const colors = this.resolveColors();
    const color = colors[0];
    const step = this.step;

    // Build a value lookup
    const cells = new Map<string, number>();
    let maxVal = 0;
    for (const cell of data) {
      const key = `${cell.row},${cell.col}`;
      cells.set(key, cell.value);
      if (cell.value > maxVal) maxVal = cell.value;
    }
    if (maxVal === 0) maxVal = 1;

    // Center the grid
    const gridW = this.cols * step - this.gap;
    const gridH = this.rows * step - this.gap;
    const offsetX = Math.max(0, Math.floor((width - gridW) / 2));
    const offsetY = Math.max(0, Math.floor((height - gridH) / 2));

    // Parse the base color to RGB for alpha blending
    const rgb = this._parseColor(color);

    for (let r = 0; r < this.rows; r++) {
      for (let c = 0; c < this.cols; c++) {
        const key = `${r},${c}`;
        const value = cells.get(key) ?? 0;
        const normalized = value / maxVal;

        if (normalized < 0.02) continue; // Skip empty cells

        const alpha = 0.08 + normalized * 0.92;
        const cellColor = `rgba(${rgb[0]}, ${rgb[1]}, ${rgb[2]}, ${alpha})`;

        const x = offsetX + c * step;
        const y = offsetY + r * step;

        // Only glow on high-value cells for performance
        this.drawPixelBlock(ctx, x, y, cellColor, normalized > 0.5);
      }
    }
    this.clearGlow(ctx);
  }

  protected hitTest(px: number, py: number, width: number, height: number) {
    const step = this.step;
    const gridW = this.cols * step - this.gap;
    const gridH = this.rows * step - this.gap;
    const offsetX = Math.max(0, Math.floor((width - gridW) / 2));
    const offsetY = Math.max(0, Math.floor((height - gridH) / 2));

    const col = Math.floor((px - offsetX) / step);
    const row = Math.floor((py - offsetY) / step);

    if (col < 0 || col >= this.cols || row < 0 || row >= this.rows) {
      return { index: -1, value: null, label: null };
    }

    const data = this.items as Array<{ row: number; col: number; value: number }>;
    const cell = data?.find((c) => c.row === row && c.col === col);

    return {
      index: row * this.cols + col,
      value: cell?.value ?? 0,
      label: `r${row} c${col}`,
      row,
      col,
    };
  }

  private _parseColor(color: string): [number, number, number] {
    // Use a temporary canvas to parse any CSS color
    const ctx = document.createElement("canvas").getContext("2d")!;
    ctx.fillStyle = color;
    ctx.fillRect(0, 0, 1, 1);
    const d = ctx.getImageData(0, 0, 1, 1).data;
    return [d[0], d[1], d[2]];
  }
}
