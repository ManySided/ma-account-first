<template>
  <q-layout view="lHh Lpr lFf">
    <q-header elevated>
      <q-toolbar>
        <q-toolbar-title>
          <q-btn color="purple" label="Меню">
            <q-menu>
              <div class="row no-wrap q-pa-md">
                <div class="column">
                  <q-btn flat @click="$router.push('account/')" label="Счета"/>
                  <q-btn flat label="Операции"/>
                  <q-btn flat label="Долги"/>
                  <q-btn flat label="Отчёты"/>
                </div>

                <q-separator vertical inset class="q-mx-lg"/>

                <div class="column items-center">
                  <div class="text-subtitle1 q-mt-md q-mb-xs">John Doe</div>
                  <q-btn
                    color="primary"
                    label="выход"
                    push
                    size="sm"
                    v-close-popup
                  />
                </div>
              </div>
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

defineOptions({
  name: 'MainLayout'
});

</script>
