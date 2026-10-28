// AutoParts Portal - Main JavaScript

document.addEventListener('DOMContentLoaded', function () {
    // Auto-dismiss alerts after 5 seconds
    const alerts = document.querySelectorAll('.alert.alert-success, .alert.alert-info');
    alerts.forEach(function (alert) {
        setTimeout(function () {
            const bsAlert = bootstrap.Alert.getOrCreateInstance(alert);
            if (bsAlert) bsAlert.close();
        }, 5000);
    });

    // Confirm delete/deactivate actions
    const dangerForms = document.querySelectorAll('form[data-confirm]');
    dangerForms.forEach(function (form) {
        form.addEventListener('submit', function (e) {
            const msg = form.getAttribute('data-confirm') || 'Are you sure?';
            if (!confirm(msg)) {
                e.preventDefault();
            }
        });
    });

    // Quantity input validation — prevent negative
    const qtyInputs = document.querySelectorAll('input[type="number"][name="quantity"]');
    qtyInputs.forEach(function (input) {
        input.addEventListener('change', function () {
            if (parseInt(this.value) < 1) {
                this.value = 1;
            }
        });
    });
});
