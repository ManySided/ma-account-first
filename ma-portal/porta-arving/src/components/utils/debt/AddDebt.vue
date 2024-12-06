<template>
  <q-btn flat round icon="add_box" @click="methodOpenDialog">
    <q-tooltip class="bg-accent">Добавить операцию по долгу</q-tooltip>
  </q-btn>

  <q-dialog
    v-model="showAddDebt"
    persistent
    backdrop-filter="blur(4px)">
    <q-card>
      <q-bar>
        <div>Добавление операции долга</div>
        <q-space/>
        <q-btn dense flat icon="close" v-close-popup>
          <q-tooltip class="bg-white text-primary">Close</q-tooltip>
        </q-btn>
      </q-bar>
      <q-card-section>
        <q-card-section class="row items-center">
          <div class="row">
            <div class="col-6 field-padding">
              <q-input outlined v-model="operationSum" label="Сумма" type="number"/>
            </div>
            <div class="col-6 field-padding">
              <custom-field-date v-model:dateVariable="operationDate"
                                 field-name="Дата создания" can-change/>
            </div>
          </div>
        </q-card-section>
      </q-card-section>

      <q-card-actions align="right">
        <q-btn flat label="Отмена" v-close-popup/>
        <q-btn flat label="Сохранить" v-close-popup @click="methodAddValue"/>
      </q-card-actions>
    </q-card>
  </q-dialog>
</template>

<script setup lang="ts">
import {ref} from 'vue';
import CustomFieldDate from 'components/utils/CustomFieldDate.vue';
import {date} from 'quasar';
import {useDebtStore} from 'stores/debtStore';

defineOptions({
  name: 'AddDebt',
  components: {CustomFieldDate},
});
const myProps = defineProps(['debtId']);
const myEmits = defineEmits(['addEvent']);

// store
const debtStore = useDebtStore();

// variables
const operationDate = ref(date.formatDate(Date.now(), 'YYYY-MM-DD'))
const operationSum = ref(0.0)
const showAddDebt = ref(false)

const methodOpenDialog = () => {
  showAddDebt.value = true
}
const methodAddValue = () => {
  showAddDebt.value = false
  updateDebt()
}

const updateDebt = () => {
  debtStore.actionAddDebtOperation(
    myProps.debtId,
    operationDate.value,
    operationSum.value,
    () => myEmits('addEvent'))
}

</script>

<style lang="sass" scoped>
.field-padding
  padding-right: 5px
  padding-bottom: 5px
</style>
