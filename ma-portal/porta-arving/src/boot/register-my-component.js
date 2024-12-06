import CustomFieldDate from 'components/utils/CustomFieldDateTime.vue'
import AddDebt from 'components/utils/debt/AddDebt.vue';

// we globally register our component with Vue
export default ({app}) => {
  app.component('custom-filed-date', CustomFieldDate)
  app.component('add-debt', AddDebt)
}
