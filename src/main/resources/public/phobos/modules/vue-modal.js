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
        disabledok: {type: Boolean, default: false},
        bodyBlocker: {type: Boolean, default: true},
        showBody: {type: Boolean, default: true},
        waiting: {type: Boolean, default: false},
        messageWait: {type: String, default: "Espere por favor..."},
        footerTmpWait: {type: Boolean, default: true},
        headerTmpWait: {type: Boolean, default: true},
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
            overlap.css({position: "absolute", width: "100%", height: (body.height() + 30), "z-index": 1001});
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
            $vue.waiting = false;
            $vue.messageWait = "Espere por favor...";
            $('#' + $vue.id).modal('show');
        },
        showWait(msg) {
            let $vue = this;

            $vue.waiting = true;
            $vue.headerTmpWait = $vue.header;
            $vue.footerTmpWait = $vue.footer;
            $vue.header = false;
            $vue.footer = false;

            if (msg != undefined) {
                $vue.messageWait = msg;
            }
        },
        hideWait() {
            let $vue = this;
            $vue.waiting = false;
            $vue.header = $vue.headerTmpWait;
            $vue.footer = $vue.footerTmpWait;
        },
        close() {
            let $vue = this;
            $('#' + $vue.id).modal('hide');
            $vue.processing = false;
            this.cancelaction();
        },
        execute(event) {
            let $vue = this;
            if ($vue.confirm) {
                $vue.beginProcessing();
            }
            $vue.okaction(event);
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
