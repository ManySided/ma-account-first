<template>
    <q-input outlined v-model="dateValueView" :label="fieldName" readonly>
      <template v-slot:prepend>
        <q-icon name="event" class="cursor-pointer" v-show="canChange">
          <q-popup-proxy cover transition-show="scale" transition-hide="scale" @before-hide="updateProxy">
            <q-date v-model="dateValueField" mask="YYYY-MM-DDTHH:mm:ss">
              <div class="row items-center justify-end">
                <q-btn v-close-popup label="Close" color="primary" flat/>
              </div>
            </q-date>
          </q-popup-proxy>
        </q-icon>
      </template>

      <template v-slot:append>
        <q-icon name="access_time" class="cursor-pointer" v-show="canChange">
          <q-popup-proxy cover transition-show="scale" transition-hide="scale" @before-hide="updateProxy">
            <q-time v-model="dateValueField" mask="YYYY-MM-DDTHH:mm:ss" format24h>
              <div class="row items-center justify-end">
                <q-btn v-close-popup label="Close" color="primary" flat/>
              </div>
            </q-time>
          </q-popup-proxy>
        </q-icon>
      </template>
    </q-input>
</template>

<script setup lang="ts">
import {ref} from 'vue';
import {date} from 'quasar';

// init
console.log('init CustomFieldDateTime')
interface DateProps {
  fieldName: string;
  canChange: boolean;
};
defineOptions({
  name: 'CustomFieldDateTime'
});
withDefaults(defineProps<DateProps>(), {fieldName: 'Дата', canChange: false});
const FORMAT_SERVER = 'YYYY-MM-DDTHH:mm:ss'
const FORMAT_VIEW = 'DD.MM.YYYY HH:mm:ss'

const convertServer2View = (dateText: string) => {
  return date.formatDate(date.extractDate(dateText, FORMAT_SERVER), FORMAT_VIEW);
}

const dateVariable = defineModel('dateVariable')
const dateValueField = ref(date.formatDate(Date.now(), FORMAT_SERVER))
const dateValueView = ref(date.formatDate(Date.now(), FORMAT_VIEW))
if (dateVariable.value) {
  dateValueField.value = dateVariable.value + '';
  dateValueView.value = convertServer2View(dateValueField.value)
}

// method
const updateProxy = () => {
    dateVariable.value = dateValueField.value
    dateValueView.value = convertServer2View(dateValueField.value)
}

</script>

<style scoped>

</style>
