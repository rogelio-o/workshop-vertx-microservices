import { ToastOptions } from 'ng2-toastr/ng2-toastr';

export class CustomToastOptions extends ToastOptions {
  showCloseButton = true;
  positionClass = 'toast-bottom-right'
}
