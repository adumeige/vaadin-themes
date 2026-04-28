import { customElement } from "lit/decorators.js";
import { PixelChartBase } from "./pixel-chart-base";

@customElement("pixel-bar-chart")
export class PixelBarChart extends PixelChartBase {
  protected renderChart(
    ctx: CanvasRenderingContext2D,
    width: number,
    height: number
  ) {
    const data = this.items as Array<{ label?: string; value: number }>;
    if (!data || data.length === 0) return;

    const colors = this.resolveColors();
    const step = this.step;
    const maxVal = Math.max(...data.map((d) => d.value), 0.001);
    const maxRows = Math.floor(height / step);

    // Center bars horizontally
    const totalWidth = data.length * step - this.gap;
    const offsetX = Math.max(0, Math.floor((width - totalWidth) / 2));

    for (let i = 0; i < data.length; i++) {
      const normalized = data[i].value / maxVal;
      const litBlocks = Math.round(normalized * maxRows);
      const color = colors[i % colors.length];
      const x = offsetX + i * step;

      for (let row = 0; row < litBlocks; row++) {
        const y = height - (row + 1) * step;
        // Glow only on top few blocks for performance
        const isTop = row >= litBlocks - 3;
        this.drawPixelBlock(ctx, x, y, color, isTop);
      }
    }
    this.clearGlow(ctx);
  }

  protected hitTest(px: number, py: number, width: number, height: number) {
    const data = this.items as Array<{ label?: string; value: number }>;
    if (!data || data.length === 0) return { index: -1, value: null, label: null };

    const step = this.step;
    const totalWidth = data.length * step - this.gap;
    const offsetX = Math.max(0, Math.floor((width - totalWidth) / 2));

    const barIndex = Math.floor((px - offsetX) / step);
    if (barIndex < 0 || barIndex >= data.length) return { index: -1, value: null, label: null };

    const d = data[barIndex];
    return { index: barIndex, value: d.value, label: d.label ?? null };
  }
}
