Vue.component("multiselect", window.VueMultiselect.default);

new Vue({
    el: '#main',
    data: {
        rolesUrl: APP.url('seguridad/rol/list'),
        cargo: {id: null},
        funcion: {id: null},
        rol: {id: null},
        cargos: [],
        funciones: [],
        dataModalCargo: {
            title: 'Relacionar Cargo',
            showaccept: false,
            modalscroll: 'modal-scroll-fix-500'
        },
        dataModalFuncion: {
            title: 'Relacionar Función',
            showaccept: false,
            modalscroll: 'modal-scroll-fix-500'
        }
    },
    computed: {
    },
    mounted() {
    },
    methods: {
        relacionarCargo: function (rol) {
            let vue = this;
            vue.rol = rol;
            vue.allCargo(rol);
            vue.dataModalCargo.title = "Cargos relacionados a " + rol.nombre;
            vue.$refs.cargomodal.open();
        },
        relacionarFuncion: function (rol) {
            let vue = this;
            vue.rol = rol;
            vue.allFuncion(rol);
            vue.dataModalFuncion.title = "Funciones relacionados a " + rol.nombre;
            vue.$refs.funcionmodal.open();
        },
        allCargo: function (rol) {
            let vue = this;
            $.ajax({
                url: APP.url('seguridad/rol/allfuncionrol'),
                type: 'POST',
                async: false,
                data: {'rol.id': rol.id, 'perfilCompania.tipo': 'CARGO'},
                success: function (response) {
                    if (response.success) {
                        vue.cargos = response.data;
                    } else {
                        notify(response.message, "error");
                    }
                },
                error: function () {
                    notify(MESSAGES.errorComunicacion, "error");
                }
            });
        },
        allFuncion: function (rol) {
            let vue = this;
            $.ajax({
                url: APP.url('seguridad/rol/allfuncionrol'),
                type: 'POST',
                async: false,
                data: {'rol.id': rol.id, 'perfilCompania.tipo': 'PERFIL'},
                success: function (response) {
                    if (response.success) {
                        vue.funciones = response.data;
                    } else {
                        notify(response.message, "error");
                    }
                },
                error: function () {
                    notify(MESSAGES.errorComunicacion, "error");
                }
            });
        },
        saveCargo: function (rol) {
            let vue = this;
            if (!$('#formCargo').parsley().validate() == true) {
                return;
            }
            $.ajax({
                url: APP.url('seguridad/rol/savefuncionrol'),
                type: 'POST',
                async: false,
                data: $('#formCargo').serialize(),
                success: function (response) {
                    if (response.success) {
                        vue.allCargo(rol);
                        vue.cargo = {id: null};
                    } else {
                        notify(response.message, "error");
                    }
                },
                error: function () {
                    notify(MESSAGES.errorComunicacion, "error");
                }
            });
        },
        saveFuncion: function (rol) {
            let vue = this;
            if (!$('#formFuncion').parsley().validate() == true) {
                return;
            }
            $.ajax({
                url: APP.url('seguridad/rol/savefuncionrol'),
                type: 'POST',
                async: false,
                data: $('#formFuncion').serialize(),
                success: function (response) {
                    if (response.success) {
                        vue.allFuncion(rol);
                        vue.funcion = {id: null};
                    } else {
                        notify(response.message, "error");
                    }
                },
                error: function () {
                    notify(MESSAGES.errorComunicacion, "error");
                }
            });
        },
        updateEstadoFuncionRol: function (funcionrol, estado) {
            let vue = this;
            swal({
                text: "¿Está seguro que desea cambiar el estado de " + funcionrol.perfilCompania.nombre + "?",
                icon: "warning",
                type: "warning",
                dangerMode: true,
                showCancelButton: true,
                closeOnConfirm: false,
                buttons: {
                    cancel: "No",
                    confirm: "Si, estoy seguro"
                }
            }).then((accept) => {
                if (accept) {
                    vue.changeEstado(funcionrol, estado);
                }
            });
        },
        changeEstado: function (funcionrol, estado) {
            let vue = this;
            $.ajax({
                url: APP.url('seguridad/rol/cambiarEstado'),
                type: 'POST',
                async: false,
                data: {id: funcionrol.id, estado: estado},
                success: function (response) {
                    if (response.success) {
                        if (funcionrol.perfilCompania.tipo == 'CARGO') {
                            vue.allCargo(funcionrol.rol);
                        } else {
                            vue.allFuncion(funcionrol.rol);
                        }
                    } else {
                        notify(response.message, "error");
                    }
                },
                error: function () {
                    notify(MESSAGES.errorComunicacion, "error");
                }
            });
        }
    }
});