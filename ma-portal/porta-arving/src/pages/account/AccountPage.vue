<template>
  <q-page>
    <div class="q-pa-md example-row-equal-width">
      <div class="row justify-center">
        <div class="col-md-8" style="padding-left: 5px">
          <q-card flat bordered>
            <q-card-section>
              <div class="q-gutter-y-md column">
                <q-input
                  outlined
                  v-model="thisAccount.name"
                  label="Название"
                  hide-bottom-space
                  :error="!isValidFieldName"
                  error-message="Название должно быть заполнено"/>
                <q-input outlined v-model="thisAccount.comment" type="textarea" label="Комментарий"/>
                <q-input outlined v-model="thisAccount.startSum"
                         type="number"
                         min="0"
                         :error="!isValidFieldStartSum"
                         error-message="Начальная сумма должна быть заполнена"
                         hide-bottom-space
                         label="Начальная сумма"/>
                <q-input outlined v-model="thisAccount.currentSum"
                         type="number"
                         readonly
                         label="Текущая сумма"/>
                <q-select outlined
                          v-model="thisAccount.currency"
                          :options="storeDict.currency"
                          option-value="id"
                          option-label="name"
                          emit-value
                          hide-bottom-space
                          :error="!isValidFieldCurrency"
                          error-message="Валюта должна быть указана"
                          label="Валюта"/>
                <custom-field-date-time
                  v-model:dateVariable="thisAccount.created"
                  field-name="Дата создания"
                  can-change/>
              </div>
            </q-card-section>

            <q-separator dark/>

            <q-card-actions>
              <q-btn flat @click="openDialog">Сохранить</q-btn>
              <q-btn flat v-if="thisAccount.id!=null" @click="removeDialog">Удалить</q-btn>
            </q-card-actions>
          </q-card>
        </div>
      </div>
    </div>

    <q-dialog v-model="showDialogSave" persistent>
      <q-card style="width: 700px; max-width: 80vw;">
        <q-card-section class="row items-center">
          <q-avatar icon="info" color="primary" text-color="white"/>
          <span class="q-ml-sm">Сохранить счёт <b>{{ thisAccount.name }}</b>?</span>
        </q-card-section>

        <q-card-actions align="right">
          <q-btn flat label="Сохранить" color="primary" @click="saveAccount" v-close-popup/>
          <q-btn flat label="Отмена" color="primary" v-close-popup/>
        </q-card-actions>
      </q-card>
    </q-dialog>

    <q-dialog v-model="showDialogRemove" persistent>
      <q-card style="width: 700px; max-width: 80vw;">
        <q-card-section class="row items-center">
          <q-avatar icon="delete" color="primary" text-color="white"/>
          <span class="q-ml-sm">Удалить счёт <b>{{ thisAccount.name }}</b>?</span>
        </q-card-section>

        <q-card-actions align="right">
          <q-btn flat label="Удалить" color="primary" @click="removeAccount" v-close-popup/>
          <q-btn flat label="Отмена" color="primary" v-close-popup/>
        </q-card-actions>
      </q-card>
    </q-dialog>
  </q-page>
</template>

<script lang="ts">
import {computed, defineComponent, ref} from 'vue';
import {useDictionaryStore} from 'stores/dictStore';
import {useAccountStore} from 'stores/accountStore';
import Account from 'src/model/dto/AccountDto';
import CustomFieldDateTime from 'components/utils/CustomFieldDateTime.vue';
import {date, Notify} from 'quasar';
import {useStuffStore} from 'stores/stuffStore';

// init variable
const now = date.formatDate(Date.now(), 'YYYY-MM-DDTHH:mm:ss');

export default defineComponent({
  name: 'AccountPage',
  components: {CustomFieldDateTime},
  setup() {
    // init store
    const storeStuff = useStuffStore();
    const storeDict = useDictionaryStore();
    const storeAccount = useAccountStore();

    // settings
    const thisAccount = ref({
      name: '',
      comment: '',
      startSum: 0,
      currentSum: 0,
      created: now,
      actual: true
    } as Account);

    const showDialogSave = ref(false);
    const showDialogRemove = ref(false);
    // validate parameters
    const isValidFieldName = computed(() => thisAccount.value && thisAccount.value.name && thisAccount.value.name.length > 0)
    const isValidFieldStartSum = computed(() => thisAccount.value && thisAccount.value.startSum > 0)
    const isValidFieldCurrency = computed(() => thisAccount.value && !!thisAccount.value.currency)

    const catchResponse = (response: Account) => {
      thisAccount.value = response
    }

    // upload data
    storeStuff.actionUpdateTitlePage('Текущий счёт');
    storeDict.upLoadDictionary();
    if (storeStuff.getAccountId)
      storeAccount.loadAccountById(
        storeStuff.getAccountId,
        catchResponse
      );

    // methods
    const newAccount = () => {
      thisAccount.value = {
        name: '',
        comment: '',
        startSum: 0,
        currentSum: 0,
        created: date.formatDate(Date.now(), 'YYYY-MM-DDTHH:mm:ss'),
        actual: true
      } as Account;
    }
    const openDialog = () => {
      // произвести валидацию
      if (isValidFieldName.value && isValidFieldStartSum.value && isValidFieldCurrency.value) {
        // открыть окно
        showDialogSave.value = true
      } else {
        // сообщить о незаполненных полях
        Notify.create({
          color: 'negative',
          position: 'top',
          message: 'Валидация формы не пройдена',
          icon: 'report_problem'
        })
      }
    }
    const removeDialog = () => {
      showDialogRemove.value = true
    }
    const saveAccount = () => {
      storeAccount.saveAccount(thisAccount.value)
    }
    const removeAccount = () => {
      storeAccount.removeAccount(thisAccount.value)
      storeAccount.loadAccounts()
    }

    return {
      // store
      storeDict,
      storeAccount,
      // methods
      newAccount,
      openDialog,
      removeDialog,
      saveAccount,
      removeAccount,
      // variable
      thisAccount,
      showDialogSave,
      showDialogRemove,

      isValidFieldName,
      isValidFieldStartSum,
      isValidFieldCurrency,
    }
  }
});
</script>

<style scoped>

</style>
