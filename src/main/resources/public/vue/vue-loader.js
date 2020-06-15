var VueLoader = {
    methods: {
        showLoader(title) {
            if (title == null) {
                $('body').loadingModal({text: ''});
                $('body').loadingModal('animation', 'threeBounce');
            } else {
                $('body').loadingModal({text: title});
                $('body').loadingModal('animation', 'threeBounce');
            }
        },
        hideLoader() {
            setTimeout(function () {
                $('body').loadingModal('hide');
                setTimeout(function () {
                    $('body').loadingModal('destroy');
                }, 1000);
            }, 1000);
        }
    }
}