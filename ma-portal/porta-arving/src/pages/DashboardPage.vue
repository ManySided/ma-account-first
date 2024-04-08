<template>
  <q-page>
    <custom-field-date-time v-model:dateVariable="modelDate" field-name="Дата создания" can-change/>
    <div>
      {{modelDate}}
    </div>
    <div class="q-pa-md row items-start q-gutter-md">
      <q-card class="my-card">
        <q-card-section>
          Добро пожаловать
          <q-btn flat @click="$router.push('workspace/account')" label="Счета"/>
        </q-card-section>
      </q-card>
    </div>
    <div class="q-pa-md row items-start q-gutter-md">
      <q-card class="my-card">
        <q-card-section>
          Account statistic
        </q-card-section>
      </q-card>
    </div>
  </q-page>
</template>

<script lang="ts">
import {defineComponent, ref} from 'vue';
import CustomFieldDateTime from 'components/utils/CustomFieldDateTime.vue';

export default defineComponent({
  name: 'DashboardPage',
  components: {CustomFieldDateTime},
  setup() {
    const modelDate = ref('2024-03-29T14:50:41');
    return {
      modelDate
    }
  }
});
</script>

<style scoped>

</style>
