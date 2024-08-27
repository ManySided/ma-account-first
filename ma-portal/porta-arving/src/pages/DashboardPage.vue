<template>
  <q-page>
    <div class="row" style="padding: 10px">
      <div class="column">
        <q-btn flat color="primary" @click="showViewCreateAccountMethod">Добавить счёт</q-btn>
      </div>
      <div class="column">
        <q-toggle v-model="showDeleteAccounts" label="показать удалённые"/>
      </div>
    </div>
    <div class="row justify-center">
      <div v-for="(item, index) in storeAccount.getAccounts"
           :key="index">
        <div class="column"
             style="padding: 10px" v-if="item.actual || showDeleteAccounts">
          <q-card class="my-card">
            <q-card-section style="color: #373783; background-color: #fafafa">
              <div :class="item.actual?'text-h6 activity-account':'text-h6 not-activity-account'">{{ item.name }}</div>
              <div class="text-subtitle2">{{ formattedNumber(item.currentSum) }}
                <q-icon :name="item.currency.symbol"/>
              </div>
            </q-card-section>

            <q-separator/>

            <q-card-actions vertical>
              <q-btn flat @click="$router.push({name:'tickets', params: {accountId: item.id}})">
                Операции
              </q-btn>
            </q-card-actions>
          </q-card>
        </div>
      </div>
    </div>

    <q-dialog
      v-model="viewCreateAccount"
      persistent
      backdrop-filter="blur(4px)">
      <q-card style="width: 700px; max-width: 80vw;">
        <q-bar>
          <div>Создание счёта</div>
          <q-space/>
          <q-btn dense flat icon="close" v-close-popup>
            <q-tooltip class="bg-white text-primary">Close</q-tooltip>
          </q-btn>
        </q-bar>
        <q-card-section>
          <account-modal v-model:accountVariable="newAccount"/>
        </q-card-section>

        <q-card-actions align="right">
          <q-btn flat v-close-popup>Отмена</q-btn>
          <q-btn flat @click="createAccountMethod">Сохранить</q-btn>
        </q-card-actions>
      </q-card>
    </q-dialog>
  </q-page>
</template>

<script lang="ts">
import {defineComponent, ref} from 'vue';
import {useAccountStore} from 'stores/accountStore';
import {createEmptyAccount, isValidAccount} from 'src/model/dto/AccountDto';
import AccountModal from 'components/utils/AccountModal.vue';
import {Notify} from 'quasar';

export default defineComponent({
  name: 'DashboardPage',
  components: {AccountModal},
  setup() {
    // init
    const storeAccount = useAccountStore();

    // load data
    storeAccount.loadAccounts()

    // variable
    const showDeleteAccounts = ref(false);
    const viewCreateAccount = ref(false);
    const newAccount = ref(createEmptyAccount())

    // methods
    const formattedNumber = (n: number) => {
      if (n)
        return n.toLocaleString();
      return '0,00';
    }
    const showViewCreateAccountMethod = () => {
      newAccount.value = createEmptyAccount();
      viewCreateAccount.value = true;
    }
    const createAccountMethod = () => {
      if (isValidAccount(newAccount.value)) {
        viewCreateAccount.value = false;
        storeAccount.saveAccount(
          newAccount.value,
          () => storeAccount.loadAccounts())
      } else {
        Notify.create({
          color: 'negative',
          position: 'top',
          message: 'Валидация формы не пройдена',
          icon: 'report_problem'
        })
      }
    }
    return {
      storeAccount,
      showDeleteAccounts,
      viewCreateAccount,
      newAccount,
      formattedNumber,
      showViewCreateAccountMethod,
      createAccountMethod
    }
  }
});
</script>

<style lang="sass" scoped>
.my-card
  width: 500px

.not-activity-account
  text-decoration: line-through

.activity-account
  text-decoration: none
</style>
