<template>
    <div class="row justify-center">
      <div class="col-md-12" style="padding-left: 5px">
        <q-card flat bordered>
          <q-card-section>
            <div class="q-gutter-y-md column">
              <q-input
                outlined
                v-model="accountVariable.name"
                label="Название"
                hide-bottom-space
                :error="!isValidFieldName"
                error-message="Название должно быть заполнено"/>
              <q-input outlined v-model="accountVariable.comment" type="textarea" label="Комментарий"/>
              <q-input outlined v-model="accountVariable.startSum"
                       type="number"
                       min="0"
                       :error="!isValidFieldStartSum"
                       error-message="Начальная сумма должна быть заполнена"
                       hide-bottom-space
                       label="Начальная сумма"/>
              <!--<q-input outlined v-model="accountVariable.currentSum"
                       type="number"
                       readonly
                       label="Текущая сумма"/>-->
              <q-select outlined
                        v-model="accountVariable.currency"
                        :options="storeDict.currency"
                        option-value="id"
                        option-label="name"

                        hide-bottom-space
                        :error="!isValidFieldCurrency"
                        error-message="Валюта должна быть указана"
                        label="Валюта"/>
            </div>
          </q-card-section>
        </q-card>
      </div>
    </div>
</template>

<script setup lang="ts">
import Account, {createEmptyAccount} from 'src/model/dto/AccountDto';
import {computed} from 'vue';
import {useDictionaryStore} from 'stores/dictStore';

defineOptions({
  name: 'AccountModal'
});
const accountVariable = defineModel<Account>('accountVariable', {
  default: createEmptyAccount()
})

// store
const storeDict = useDictionaryStore();
storeDict.upLoadDictionary();

// validate parameters
const isValidFieldName = computed(() => accountVariable.value && accountVariable.value.name && accountVariable.value.name.length > 0)
const isValidFieldStartSum = computed(() => accountVariable.value && accountVariable.value.startSum >= 0)
const isValidFieldCurrency = computed(() => accountVariable.value && !!accountVariable.value.currency)
</script>

<style scoped>

</style>
