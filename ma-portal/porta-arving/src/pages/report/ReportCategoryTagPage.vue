<template>
  <div class="q-pa-md example-row-equal-width">
    <div class="row">
    </div>
    <div class="row">
      <div class="col-2">
        <custom-field-date v-model:dateVariable="beginDate" field-name="дата начала" can-change/>
      </div>
      <div class="col-2">
        <custom-field-date v-model:dateVariable="endDate" field-name="дата окончания" can-change/>
      </div>
    </div>
  </div>
</template>

<script lang="ts">
import {defineComponent, ref} from 'vue';
import {useStuffStore} from 'stores/stuffStore';
import CustomFieldDate from 'components/utils/CustomFieldDate.vue';

export default defineComponent({
  name: 'ReportCategoryTagPage',
  components: {CustomFieldDate},

  setup() {
    // init
    const windowWidth = ref(window.innerWidth)
    const windowHeight = ref(window.innerHeight)

    const storeStuff = useStuffStore()

    // load data
    storeStuff.actionUpdateTitlePage('Отчётная форма КТ-1')
    storeStuff.actionUpdateAccountData()

    // variable
    const beginDate = ref(Date.now())
    const endDate = ref(Date.now())

    return {
      // init
      storeStuff,
      // variable
      windowWidth,
      windowHeight,
      beginDate,
      endDate
    }
  }
})
</script>


<style scoped lang="sass">

</style>
