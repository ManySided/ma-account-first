import CustomFieldDate from 'components/utils/CustomFieldDateTime.vue'

// we globally register our component with Vue
export default ({app}) => {
  app.component('custom-filed-date', CustomFieldDate)
}
