import { customElement } from "lit/decorators.js";
import { PixelChartBase } from "./pixel-chart-base";

@customElement("pixel-scatter")
export class PixelScatter extends PixelChartBase {
  protected renderChart(
    ctx: CanvasRenderingContext2D,
    width: number,
    height: number
  ) {
    const data = this.items as Array<{
      x: number;
      y: number;
      size: number;
      colorIndex: number;
    }>;
    if (!data || data.length === 0) return;

    const colors = this.resolveColors();
    const step = this.step;
    const maxCols = Math.floor(width / step);
    const maxRows = Math.floor(height / step);

    for (const point of data) {
      const col = Math.round(point.x * (maxCols - 1));
      const row = Math.round((1 - point.y) * (maxRows - 1)); // y=0 is bottom
      const color = colors[point.colorIndex % colors.length];
      const dotSize = Math.max(1, Math.round(point.size));

      // Draw NxN pixel blocks
      for (let dr = 0; dr < dotSize; dr++) {
        for (let dc = 0; dc < dotSize; dc++) {
          const x = (col + dc) * step;
          const y = (row + dr) * step;
          if (x + this.pixelSize <= width && y + this.pixelSize <= height) {
            this.drawPixelBlock(ctx, x, y, color, true);
          }
        }
      }
    }
    this.clearGlow(ctx);
  }

  protected hitTest(px: number, py: number, width: number, height: number) {
    const data = this.items as Array<{ x: number; y: number; size: number; colorIndex: number }>;
    if (!data || data.length === 0) return { index: -1, value: null, label: null };

    const step = this.step;
    const maxCols = Math.floor(width / step);
    const maxRows = Math.floor(height / step);

    // Find closest point
    let bestIdx = -1;
    let bestDist = Infinity;
    for (let i = 0; i < data.length; i++) {
      const col = Math.round(data[i].x * (maxCols - 1));
      const row = Math.round((1 - data[i].y) * (maxRows - 1));
      const cx = col * step + this.pixelSize / 2;
      const cy = row * step + this.pixelSize / 2;
      const dist = Math.hypot(px - cx, py - cy);
      const hitRadius = data[i].size * step;
      if (dist < hitRadius && dist < bestDist) {
        bestDist = dist;
        bestIdx = i;
      }
    }

    if (bestIdx < 0) return { index: -1, value: null, label: null };
    const p = data[bestIdx];
    return { index: bestIdx, value: p.size, label: `(${p.x.toFixed(2)}, ${p.y.toFixed(2)})` };
  }
}
