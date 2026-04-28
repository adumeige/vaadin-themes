import { customElement, property } from "lit/decorators.js";
import { PixelChartBase } from "./pixel-chart-base";

@customElement("pixel-sparkline")
export class PixelSparkline extends PixelChartBase {
  @property({ type: Boolean }) fillArea = true;

  protected renderChart(
    ctx: CanvasRenderingContext2D,
    width: number,
    height: number
  ) {
    const data = this.items as number[];
    if (!data || data.length < 2) return;

    const colors = this.resolveColors();
    const color = colors[0];
    const step = this.step;
    const maxVal = Math.max(...data, 0.001);
    const minVal = Math.min(...data, 0);
    const range = maxVal - minVal || 1;

    const maxCols = Math.floor(width / step);
    const maxRows = Math.floor(height / step);

    // Map data points to pixel grid coordinates
    const points: Array<{ col: number; row: number }> = [];
    for (let i = 0; i < data.length; i++) {
      const col = Math.round((i / (data.length - 1)) * (maxCols - 1));
      const normalized = (data[i] - minVal) / range;
      const row = maxRows - 1 - Math.round(normalized * (maxRows - 1));
      points.push({ col, row });
    }

    // Fill area (dimmed)
    if (this.fillArea) {
      // Build a column → topRow map by interpolating between points
      const colTop = new Map<number, number>();
      for (let p = 0; p < points.length - 1; p++) {
        const from = points[p];
        const to = points[p + 1];
        this._bresenham(from.col, from.row, to.col, to.row, (c, r) => {
          const existing = colTop.get(c);
          if (existing === undefined || r < existing) {
            colTop.set(c, r);
          }
        });
      }

      const fillColor = color;
      ctx.globalAlpha = 0.15;
      for (const [col, topRow] of colTop) {
        for (let row = topRow + 1; row < maxRows; row++) {
          ctx.fillStyle = fillColor;
          ctx.fillRect(col * step, row * step, this.pixelSize, this.pixelSize);
        }
      }
      ctx.globalAlpha = 1.0;
    }

    // Draw the line using Bresenham between consecutive points
    for (let p = 0; p < points.length - 1; p++) {
      const from = points[p];
      const to = points[p + 1];
      this._bresenham(from.col, from.row, to.col, to.row, (col, row) => {
        this.drawPixelBlock(ctx, col * step, row * step, color, true);
      });
    }

    this.clearGlow(ctx);
  }

  protected hitTest(px: number, _py: number, width: number, _height: number) {
    const data = this.items as number[];
    if (!data || data.length < 2) return { index: -1, value: null, label: null };

    const step = this.step;
    const maxCols = Math.floor(width / step);
    // Find closest data point by x position
    const col = Math.round(px / step);
    const dataIndex = Math.round((col / (maxCols - 1)) * (data.length - 1));
    const clamped = Math.max(0, Math.min(data.length - 1, dataIndex));
    return { index: clamped, value: data[clamped], label: `[${clamped}]` };
  }

  /** Bresenham line algorithm on pixel grid */
  private _bresenham(
    x0: number,
    y0: number,
    x1: number,
    y1: number,
    plot: (x: number, y: number) => void
  ) {
    let dx = Math.abs(x1 - x0);
    let dy = -Math.abs(y1 - y0);
    let sx = x0 < x1 ? 1 : -1;
    let sy = y0 < y1 ? 1 : -1;
    let err = dx + dy;

    while (true) {
      plot(x0, y0);
      if (x0 === x1 && y0 === y1) break;
      let e2 = 2 * err;
      if (e2 >= dy) {
        err += dy;
        x0 += sx;
      }
      if (e2 <= dx) {
        err += dx;
        y0 += sy;
      }
    }
  }
}
