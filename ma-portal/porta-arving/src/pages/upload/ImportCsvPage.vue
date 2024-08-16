<template>
  <q-page>
    <div class="q-pa-md example-row-equal-width">
      <div class="row justify-center">
        <div class="col-md-8" style="padding-left: 5px">
          <q-card flat bordered>
            <q-card-section>
              <q-file filled v-model="fileModel" label="файл импорта"/>
            </q-card-section>
            <q-separator dark/>
            <q-card-actions>
              <q-btn outline class="full-width" @click="importMethod">Загрузить</q-btn>
            </q-card-actions>
          </q-card>
        </div>
      </div>
    </div>
  </q-page>
</template>

<script lang="ts">
import {defineComponent, ref} from 'vue';
import {useStuffStore} from 'stores/stuffStore';
import {useImportStore} from 'stores/importStore';

export default defineComponent({
  name: 'ImportCsvPage',
  setup() {
    const storeStuff = useStuffStore();
    const storeImport = useImportStore();

    storeStuff.actionUpdateTitlePage('Импорт данных');

    const fileModel = ref(null);

    const importMethod = () => {
      if (fileModel.value)
        storeImport.importCsv(storeStuff.getAccountId, fileModel.value)
      console.log("file:" + fileModel.value)
    };
    return {
      fileModel,
      importMethod
    }
  }
})
</script>

<style scoped>

</style>
