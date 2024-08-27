import {AxiosError} from 'axios';
import {Notify} from 'quasar';

type ErrorBack = {
  error: string,
  timestamp: Date
}

export function handleError(errorData: AxiosError) {
  let errorMsg = 'Ошибка выполнения операции'
  if (errorData.response) {
    const errorResponse = errorData.response;

    if (errorResponse.data) {
      const errorMessage = errorResponse.data as ErrorBack
      if (errorMessage.error)
        errorMsg = 'Ошибка: ' + errorMessage.error;
    }
  }

  Notify.create({
    color: 'negative',
    position: 'top',
    message: errorMsg,
    icon: 'report_problem'
  })
}
