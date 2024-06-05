<template>
  <q-page>
    <div style="padding: 5px">
      <operation-edit-row :account-id="1" v-model:operationVariable="modelOperation"/>
    </div>
    <div>
      <q-btn @click="updateCategory"/>
    </div>
  </q-page>
</template>

<script lang="ts">
import {defineComponent, ref} from 'vue';
import OperationEditRow from 'components/utils/OperationEditRow.vue';

export default defineComponent({
  name: 'DashboardPage',
  components: {OperationEditRow},
  setup() {
    const modelOperation = ref({
      sum: 0,
      name: '',
      comment: '',
      category: {}
    });
    const modelDataEmpty = ref();
    const updateCategory = () => {
      if (modelOperation.value &&  modelOperation.value.category) {
        modelOperation.value.category = {
          id: 3,
          name: 'Одежда'
        }
      }
    }
    return {
      modelOperation,
      modelDataEmpty,
      updateCategory
    }
  }
});
</script>

<style scoped>

</style>
