<template>
  <v-chart class="chart" :option="tree_data" :loading="tree_loading" />
</template>

<script>
import { use } from "echarts/core";
import { CanvasRenderer } from "echarts/renderers";
import { PieChart } from "echarts/charts";
import {
  LegendComponent,
  TitleComponent,
  TooltipComponent
} from "echarts/components";
import VChart, { THEME_KEY } from "vue-echarts";
import { defineComponent } from "vue";
import getTreeData from "@/data/_tree_data";

use([
  CanvasRenderer,
  PieChart,
  TitleComponent,
  TooltipComponent,
  LegendComponent
]);

export default defineComponent({
  name: "EchartsTree",

  components: {
    VChart
  },
  provide: {
    [THEME_KEY]: ""
  },
  data() {
    return {
      tree_data: getTreeData(),
      tree_loading: false
    };
  },
  methods: {
    updateData() {
      // this.tree_loading = true;
      this.tree_data = getTreeData();
      window.setTimeout(() => {
        this.tree_loading = false;
      }, 50);
    }
  }
});
</script>

<style scoped>
.chart {
  height: 400px;
}
</style>
