<template>
  <q-layout view="lHh Lpr lFf">
    <q-header elevated>
      <q-toolbar>
        <q-toolbar-title>
          <q-btn color="purple" label="Меню">
            <q-menu>
              <q-list dense style="min-width: 100px">
                <q-item clickable v-close-popup>
                  <q-item-section @click="$router.push('account/')">Счета</q-item-section>
                </q-item>
                <q-item clickable>
                  <q-item-section>Операции</q-item-section>
                  <q-item-section side>
                    <q-icon name="keyboard_arrow_right"/>
                  </q-item-section>

                  <q-menu anchor="top end" self="top start">
                    <q-list>
                      <q-item
                        v-for="(item, index) in storeAccount.getAccounts" :key="index"
                        dense
                        clickable
                      >
                        <q-item-section
                          @click="$router.push({name:'tickets', params: {accountId: getTicketsLink(item)}})">
                          {{ item.name }}
                        </q-item-section>
                      </q-item>
                    </q-list>
                  </q-menu>

                </q-item>
                <q-item clickable v-close-popup>
                  <q-item-section>Долги</q-item-section>
                </q-item>
              </q-list>
            </q-menu>
          </q-btn>
        </q-toolbar-title>
      </q-toolbar>
    </q-header>

    <q-page-container>
      <router-view></router-view>
    </q-page-container>
  </q-layout>
</template>

<script setup lang="ts">
import {useAccountStore} from 'stores/accountStore';
import Account from 'src/model/dto/AccountDto';

const storeAccount = useAccountStore();
storeAccount.loadAccounts();

const getTicketsLink = (item: Account) => {
  return item.id;
};

defineOptions({
  name: 'MainLayout',
});

</script>
