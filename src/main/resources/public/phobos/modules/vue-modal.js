// Ref: https://github.com/joturako/vue2-bootstrap-modal/tree/master/src

Vue.component('modal-vik', {
    template: '#modal-vik-template',
    props: {
        id: {type: String, default: null},
        header: {type: Boolean, default: false},
        footer: {type: Boolean, default: true},
        btnclose: {type: Boolean, default: true},
        title: {type: String, default: 'Modal Vik'},
        okbtn: {type: String, default: 'Aceptar'},
        okbtnprocessing: {type: String, default: '<i class="fa fa-spinner fa-pulse fa-fw"></i> Procesando...'},
        okaction: {type: Function, default: () => {
            }},
        okclass: {type: String, default: 'btn-success'},
        cancelbtn: {type: String, default: 'Cerrar'},
        cancelaction: {type: Function, default: () => {
            }},
        cancelclass: {type: String, default: 'btn-default'},
        modalsize: {type: String, default: "modal-md"},
        showaccept: {type: Boolean, default: false},
        confirm: {type: Boolean, default: false},
        modalscroll: {type: String, default: ''},
        styleModal: {type: Object, default: null},
        dataBackdrop: {type: String, default: null},
        dataKeyboard: {type: String, default: null},
        processing: {type: Boolean, default: false},
        bodyBlocker: {type: Boolean, default: true},
        showBody: {type: Boolean, default: true},
    },
    data() {
        return {
            show: false
        }
    },
    methods: {
        opaque() {
            let $vue = this;
            $vue.showBody = false;
        },
        removeOpaque() {
            let $vue = this;
            $vue.showBody = true;
        },
        blockBody() {
            let $vue = this;
            let body = $("#" + $vue.id).find("#div-body-modal");
            let overlap = $("#" + $vue.id).find("#over-body-modal");

            overlap.removeClass("hide")
            overlap.css({position: "absolute", width: "100%", height: (body.height() + 30)});
            overlap.offset({top: body.offset().top});
        },
        unlockBody() {
            let $vue = this;
            let div2 = $("#" + $vue.id).find("#over-body-modal");
            div2.addClass("hide")
        },
        beginProcessing() {
            let $vue = this;
            $vue.processing = true;
            if ($vue.bodyBlocker) {
                $vue.blockBody();
            }
        },
        stopProcessing() {
            let $vue = this;
            $vue.processing = false;
            $vue.unlockBody();
        },
        open() {
            let $vue = this;
            $('#' + $vue.id).modal('show');
        },
        close() {
            let $vue = this;
            $('#' + $vue.id).modal('hide');
            $vue.processing = false;
            this.cancelaction();
        },
        execute() {
            let $vue = this;
            if ($vue.confirm) {
                $vue.beginProcessing();
            }
            $vue.okaction();
        },
        confirmReaction(result) {
            let $vue = this;
            $vue.stopProcessing();
            if (result) {
                $vue.close();
            }
        }
    }
});
