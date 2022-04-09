<template>
  <div>
    <v-chart class="chart" :option="option" />
    <br />
    <v-chart
      class="chart"
      :option="bar_data"
      :loading="bar_loading"
      @zr:click="updateData"
    />
  </div>
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
import { defineComponent, ref } from "vue";
import getBarData from "@/data/_bar";
import { getUsrInfo } from "@/data/_getUserInfo";

use([
  CanvasRenderer,
  PieChart,
  TitleComponent,
  TooltipComponent,
  LegendComponent
]);

export default defineComponent({
  name: "HelloWorld",
  components: {
    VChart
  },
  props: {
    myMsg: String
  },
  provide: {
    [THEME_KEY]: "dark"
  },
  data() {
    return {
      bar_data: getBarData(),
      bar_loading: false
    };
  },
  methods: {
    async updateData() {
      const response = await getUsrInfo();
      console.log(response);
      this.bar_loading = true;
      this.bar_data = getBarData();
      window.setTimeout(() => {
        this.bar_loading = false;
      }, 200);
    }
  },
  setup(props) {
    const option = ref({
      title: {
        text: props.myMsg ? props.myMsg : "HelloWorld",
        left: "center"
      },
      props: {
        msg: String
      },
      tooltip: {
        trigger: "item",
        formatter: "{a} <br/>{b} : {c} ({d}%)"
      },
      legend: {
        orient: "vertical",
        left: "left",
        data: ["Direct", "Email", "Ad Networks", "Video Ads", "Search Engines"]
      },
      series: [
        {
          name: "Traffic Sources",
          type: "pie",
          radius: "55%",
          center: ["50%", "60%"],
          data: [
            { value: 335, name: "Direct" },
            { value: 310, name: "Email" },
            { value: 234, name: "Ad Networks" },
            { value: 135, name: "Video Ads" },
            { value: 1548, name: "Search Engines" }
          ],
          emphasis: {
            itemStyle: {
              shadowBlur: 10,
              shadowOffsetX: 0,
              shadowColor: "rgba(0, 0, 0, 0.5)"
            }
          }
        }
      ]
    });

    return { option };
  }
});
</script>

<style scoped>
.chart {
  height: 400px;
}
</style>
