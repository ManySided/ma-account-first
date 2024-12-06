<template>
  <q-layout view="lHh Lpr lFf">
    <q-header elevated style="background-color: #292961">
      <q-toolbar class="q-py-sm q-px-md">
        <q-toolbar-title>
          <q-btn color="white" icon="menu" flat>
            <q-menu>
              <q-list dense style="min-width: 100px">
                <q-item clickable v-close-popup>
                  <q-item-section @click="$router.push({name: 'account'})">Текущий счёт</q-item-section>
                </q-item>
                <q-item clickable v-close-popup>
                  <q-item-section @click="$router.push({name: 'category'})">Категории</q-item-section>
                </q-item>
                <q-item clickable v-close-popup>
                  <q-item-section @click="$router.push({name: 'tickets'})">Операции</q-item-section>
                </q-item>
                <q-item clickable v-close-popup>
                  <q-item-section @click="$router.push({name: 'debts'})">Долги</q-item-section>
                </q-item>
                <q-separator></q-separator>
                <q-item clickable v-close-popup>
                  <q-item-section @click="$router.push({name: 'importCsv'})">Импорт</q-item-section>
                </q-item>
              </q-list>
            </q-menu>
          </q-btn>
          <q-avatar square>
            <q-img src="~assets/logo.png"></q-img>
          </q-avatar>
          <b>Arving</b>
        </q-toolbar-title>
        <q-spinner color="orange" size="3em" v-if="storeStuff.isShowLoadState"/>
        <div class="title-page">{{ storeStuff.titlePage }}</div>
        <q-space/>
        <q-item clickable>
          <q-item-section @click="$router.push({name: 'dashboard'})">
            <q-item-label>{{ storeStuff.getAccountName }}</q-item-label>
            <q-item-label caption class="text-white">{{ storeStuff.getAccountCurrentSum }}
              {{ storeStuff.getAccountCurrencyShortName }}
            </q-item-label>
          </q-item-section>
        </q-item>
      </q-toolbar>
    </q-header>

    <q-page-container>
      <router-view></router-view>
    </q-page-container>
  </q-layout>
</template>

<script setup lang="ts">
import {useStuffStore} from 'stores/stuffStore';

const props = defineProps({accountId: Number});

const storeStuff = useStuffStore();

storeStuff.actionSetAccountId(props.accountId ?? 0);
storeStuff.actionUpdateAccountData();

defineOptions({
  name: 'MainLayout'
});

</script>

<style lang="sass" scoped>
.title-page
  text-transform: uppercase
  font-size: 26px
</style>
