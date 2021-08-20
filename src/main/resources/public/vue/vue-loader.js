var VueLoader = {
    methods: {
        showLoader(title) {
            $('body').loadingModal({text: title ? title : ' '});
            $('body').loadingModal('animation', 'doubleBounce');
        },
        hideLoader() {
            $('body').loadingModal('hide');
            setTimeout(function () {
                $('body').loadingModal('destroy');
            }, 500);
        }
    }
}