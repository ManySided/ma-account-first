<template>
  <q-page>
    <div class="row" style="padding: 10px">
      <div class="column">
        <q-btn flat color="primary" @click="$router.push({name:'debtView'})">
          Добавить долг
        </q-btn>
      </div>
      <!--<div class="column">
        <q-toggle label="показать удалённые"/>
      </div>-->
    </div>

    <div class="row justify-center">
      <div v-for="(item, index) in storeDebt.getDebts" :key="index">
        <div style="padding: 5px" v-if="item.isActive && !item.deleteFlag">
          <q-card class="my-card" flat bordered>
            <q-item>
              <q-linear-progress rounded size="10px" :value="progress(item)" color="secondary" class="q-mt-sm"/>
            </q-item>
            <q-card-section horizontal>
              <q-card-section class="q-pt-xs">
                <div class="text-h5 q-mt-sm q-mb-xs">{{ item.name }}</div>
                <div class="text-caption text-grey">
                  {{ item.description }}
                </div>
              </q-card-section>

              <q-card-section class="col-5 flex flex-center">
                <q-badge color="white" text-color="accent" :label="progressLabel(item)"/>
              </q-card-section>
            </q-card-section>

            <q-separator/>

            <q-card-actions>
              <q-btn flat round icon="receipt_long"
                     @click="$router.push({name:'debtEdit', params: {debtId: item.id}})">
                <q-tooltip class="bg-accent">Операции по долгу
                </q-tooltip>
              </q-btn>
              <add-debt v-model:debt-id="item.id" @add-event="refreshAfterAddOperation(item.id)"/>
              <q-btn flat round icon="cancel" @click="openDeleteDebt(item)">
                <q-tooltip class="bg-accent">Удалить долг</q-tooltip>
              </q-btn>
              <q-btn flat round icon="block" @click="openCloseDebt(item)">
                <q-tooltip class="bg-accent">Закрыть долг</q-tooltip>
              </q-btn>
            </q-card-actions>
          </q-card>
        </div>
      </div>
    </div>

    <!-- модальное окно удаления долга -->
    <q-dialog
      v-model="visibleDeleteView"
      persistent
      backdrop-filter="blur(4px)">
      <q-card>
        <q-bar>
          <div>Удаление долга</div>
          <q-space/>
          <q-btn dense flat icon="close" v-close-popup>
            <q-tooltip class="bg-white text-primary">Close</q-tooltip>
          </q-btn>
        </q-bar>
        <q-card-section>
          <q-card-section class="row items-center">
            <div class="row">
              Удалить долг "{{ currentDebt.name }}" ({{ progressLabel(currentDebt) }})?
            </div>
          </q-card-section>
        </q-card-section>

        <q-card-actions align="right">
          <q-btn flat label="Отмена" v-close-popup @click="visibleDeleteView=false"/>
          <q-btn flat label="Удалить" v-close-popup @click="deleteDebt"/>
        </q-card-actions>
      </q-card>
    </q-dialog>

    <!-- модальное окно закрытия долга -->
    <q-dialog
      v-model="visibleCloseView"
      persistent
      backdrop-filter="blur(4px)">
      <q-card>
        <q-bar>
          <div>Закрытие долга</div>
          <q-space/>
          <q-btn dense flat icon="close" v-close-popup>
            <q-tooltip class="bg-white text-primary">Close</q-tooltip>
          </q-btn>
        </q-bar>
        <q-card-section>
          <q-card-section class="row items-center">
            <div class="row">
              Закрыть долг "{{ currentDebt.name }}" ({{ progressLabel(currentDebt) }})?
            </div>
          </q-card-section>
        </q-card-section>

        <q-card-actions align="right">
          <q-btn flat label="Отмена" v-close-popup @click="visibleCloseView=false"/>
          <q-btn flat label="Закрыть" v-close-popup @click="closeDebt"/>
        </q-card-actions>
      </q-card>
    </q-dialog>

  </q-page>
</template>

<script lang="ts">
import {defineComponent, ref} from 'vue';
import {useStuffStore} from 'stores/stuffStore';
import {useDebtStore} from 'stores/debtStore';
import {createEmptyDebt, Debt} from 'src/model/dto/DebtDto';
import AddDebt from 'components/utils/debt/AddDebt.vue';

export default defineComponent({
  name: 'TicketListPage',
  components: AddDebt,
  setup() {
    // store
    const storeStuff = useStuffStore();
    const storeDebt = useDebtStore();
    // init
    const windowWidth = ref(window.innerWidth)
    const windowHeight = ref(window.innerHeight)
    storeStuff.actionUpdateTitlePage('Долги');

    // load data
    storeDebt.actionDebtList(storeStuff.getAccountId)

    // variable
    const currentDebt = ref(createEmptyDebt())
    const visibleDeleteView = ref(false)
    const visibleCloseView = ref(false)

    // methods
    const formattedNumber = (n: number) => {
      if (n)
        return n.toLocaleString();
      return '0,00';
    }
    const progress = (debt: Debt) => {
      return debt.sumDebtCurrent / debt.sumDebt
    }
    const progressLabel = (debt: Debt) => {
      return formattedNumber(debt.sumDebtCurrent) + ' / ' + formattedNumber(debt.sumDebt)
        + ' ' + storeStuff.getAccountCurrencyShortName
    }
    const refreshAfterAddOperation = (debtId: number | undefined) => {
      if (debtId)
        storeDebt.actionUpdateDebt(debtId)
    }
    const openCloseDebt = (debt: Debt) => {
      currentDebt.value = debt
      visibleCloseView.value = true
    }
    const openDeleteDebt = (debt: Debt) => {
      currentDebt.value = debt
      visibleDeleteView.value = true
    }
    const closeDebt = () => {
      storeDebt.actionCloseDebt(
        currentDebt.value.id,
        () => {
          if (currentDebt.value.id) {
            storeDebt.actionUpdateDebt(currentDebt.value.id)
            storeStuff.actionUpdateAccountData();
            visibleCloseView.value = false
          }
        })
    }
    const deleteDebt = () => {
      storeDebt.actionDeleteDebt(
        currentDebt.value.id,
        () => {
          if (currentDebt.value.id) {
            storeDebt.actionUpdateDebt(currentDebt.value.id)
            storeStuff.actionUpdateAccountData();
            visibleDeleteView.value = false
          }
        })
    }

    return {
      // store
      storeStuff,
      storeDebt,
      // init
      windowWidth,
      windowHeight,
      // variable
      currentDebt,
      visibleDeleteView,
      visibleCloseView,
      // methods
      formattedNumber,
      progress,
      progressLabel,
      refreshAfterAddOperation,
      openCloseDebt,
      closeDebt,
      openDeleteDebt,
      deleteDebt
    }
  }
})

</script>

<style lang="sass" scoped>
.my-card
  width: 350px

.card-style
  color: #373783
  background-color: #fafafa
</style>
