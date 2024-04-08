<template>
  <q-item
    clickable
    v-ripple
    class="rounded-borders"
    :class="$q.dark.isActive ? 'bg-grey-9 text-white' : 'bg-grey-2'"
  >
    <q-item-section>
      <q-item-label>
        {{fieldName}}
      </q-item-label>
      {{convertServer2View()}}
    </q-item-section>
    <q-item-section side>
      <q-icon name="event" class="cursor-pointer" v-show="canChange">
        <q-popup-proxy cover transition-show="scale" transition-hide="scale">
          <q-date v-model="dateVariable" mask="YYYY-MM-DDTHH:mm:ss">
            <div class="row items-center justify-end">
              <q-btn v-close-popup label="Close" color="primary" flat/>
            </div>
          </q-date>
        </q-popup-proxy>
      </q-icon>
    </q-item-section>
    <q-item-section side>
      <q-icon name="access_time" class="cursor-pointer" v-show="canChange">
        <q-popup-proxy cover transition-show="scale" transition-hide="scale">
          <q-time v-model="dateVariable" mask="YYYY-MM-DDTHH:mm:ss" format24h>
            <div class="row items-center justify-end">
              <q-btn v-close-popup label="Close" color="primary" flat/>
            </div>
          </q-time>
        </q-popup-proxy>
      </q-icon>
    </q-item-section>
  </q-item>
</template>

<script setup lang="ts">
import {date} from 'quasar';

// init
interface DateProps {
  fieldName: string;
  canChange: boolean;
}
defineOptions({
  name: 'CustomFieldDateTime'
});
withDefaults(defineProps<DateProps>(), {fieldName: 'Дата', canChange: false});

const FORMAT_SERVER = 'YYYY-MM-DDTHH:mm:ss'
const FORMAT_VIEW = 'DD.MM.YYYY HH:mm:ss'


const dateVariable = defineModel('dateVariable')
const convertServer2View = () => {
  return date.formatDate(date.extractDate(dateVariable.value as string, FORMAT_SERVER), FORMAT_VIEW);
}
</script>

<style scoped>

</style>
