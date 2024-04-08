import {boot} from 'quasar/wrappers'
import axios from 'axios'

const api = axios.create({
  baseURL: 'http://localhost:8091',
  // withCredentials: true
})

export default boot(({app}) => {
  // for use inside Vue files (Options API) through this.$axios and this.$api
  api.interceptors.request.use(
    (config) => {
      // Only deal with bearer token, if keycloak is enabled
      if (app.config.globalProperties.$keycloak) {
        const keycloak = app.config.globalProperties.$keycloak;
        if (keycloak.authenticated) {

          const keycloak = app.config.globalProperties.$keycloak;
          const token = keycloak.token;
          if (token && config.headers) {
            config.headers.Authorization = `Bearer ${token}`;
          }
        }
      }
      return config;
    }
  );

  app.config.globalProperties.$axios = axios
  // ^ ^ ^ this will allow you to use this.$axios (for Vue Options API form)
  //       so you won't necessarily have to import axios in each vue file

  app.config.globalProperties.$api = api
  // ^ ^ ^ this will allow you to use this.$api (for Vue Options API form)
  //       so you can easily perform requests against your app's API
})

export {axios, api}
