<template>
  <q-page>
    <div class="q-pa-md q-gutter-sm">
      <q-breadcrumbs>
        <q-breadcrumbs-el label="Счёт"/>
        <q-breadcrumbs-el label="Новый - Выбранный"/>
      </q-breadcrumbs>
    </div>

    <div class="q-pa-md example-row-equal-width">
      <div class="row">
        <div class="col">
          <q-toggle v-model="isShowActivity" label="Показывать только активные счета"/>
        </div>
      </div>
      <div class="row">
        <div class="col-md-3">
          <q-list bordered separator class="rounded-borders">
            <q-item clickable v-ripple
                    v-for="(item, index) in storeAccount.getAccounts" :key="index"
                    @click="changeAccount(item)"
            >
              <q-item-section>
                <q-item-label><b>{{ item.name }}</b></q-item-label>
                <q-item-label><i>{{ item.comment }}</i></q-item-label>
              </q-item-section>
              <q-item-section side top>
                <q-item-label caption>{{ item.currentSum }}</q-item-label>
              </q-item-section>
            </q-item>
          </q-list>
          <div style="padding-top: 5px">
            <q-btn color="primary" class="col-12" style="width: 100%" @click="newAccount">Создать</q-btn>
          </div>
        </div>
        <div class="col-md-8" style="padding-left: 5px">
          <q-card flat bordered class="my-card">
            <q-card-section>
              <div class="q-gutter-y-md column">
                <q-input outlined v-model="thisAccount.name" label="Название"/>
                <q-input outlined v-model="thisAccount.comment" type="textarea" label="Комментарий"/>
                <q-input outlined v-model="thisAccount.startSum" type="number" label="Начальная сумма"/>
                <q-input outlined v-model="thisAccount.currentSum" type="number" readonly label="Текущая сумма"/>
                <q-select outlined
                          v-model="thisAccount.currency"
                          :options="storeDict.currency"
                          option-value="id"
                          option-label="name"
                          emit-value
                          map-options
                          label="Валюта"/>
                <custom-field-date-time
                  v-model:dateVariable="thisAccount.created"
                  field-name="Дата создания"
                  can-change/>
              </div>
            </q-card-section>

            <q-separator dark/>

            <q-card-actions>
              <q-btn flat @click="saveAccount">Сохранить</q-btn>
              <q-btn flat>Удалить</q-btn>
            </q-card-actions>
          </q-card>
        </div>
      </div>
    </div>
  </q-page>
</template>

<script lang="ts">
import {defineComponent, ref} from 'vue';
import {useDictionaryStore} from 'stores/dictStore';
import {useAccountStore} from 'stores/accountStore';
import Account from 'src/model/dto/AccountDto';
import CustomFieldDateTime from 'components/utils/CustomFieldDateTime.vue';
import {date} from 'quasar';

// init variable
const now = date.formatDate(Date.now(), 'YYYY-MM-DDTHH:mm:ss');

export default defineComponent({
  name: 'AccountPage',
  components: {CustomFieldDateTime},
  setup() {

    // init store
    const storeDict = useDictionaryStore();
    const storeAccount = useAccountStore();

    // settings
    const isShowActivity = ref(true);
    const thisAccount = ref({
      name: '',
      comment: '',
      startSum: 0,
      currentSum: 0,
      currency: {
        id: 1
      },
      created: now,
      actual: true
    } as Account);

    // upload data
    storeDict.upLoadDictionary();
    storeAccount.loadAccounts()

    // methods
    const changeAccount = (item: Account) => {
      thisAccount.value = item;
    }
    const newAccount = () => {
      thisAccount.value = {
        name: '',
        comment: '',
        startSum: 0,
        currentSum: 0,
        currency: {
          id: 1
        },
        created: '',
        actual: true
      } as Account;
    }
    const saveAccount = () => {
      console.log(thisAccount.value)
    }

    const model = ref(null);

    const fieldTxt = ref('');
    const date = ref('');
    return {
      // store
      storeDict,
      storeAccount,
      // methods
      changeAccount,
      newAccount,
      saveAccount,
      // variable
      thisAccount,
      model,

      isShowActivity,
      fieldTxt,
      date
    }
  }
});
</script>

<style scoped>

</style>
