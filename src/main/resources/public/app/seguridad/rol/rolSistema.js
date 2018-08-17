new Vue({
    el: '#main',
    mixins: [VueLoader],
    data: {
        rolesUrl: APP.url('seguridad/rol/list'),
        cargo: {id: null},
        funcion: {id: null},
        rol: {id: null},
        rolSuperior: {id: null},
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
        },
        dataModalRol: {
            title: 'Nuevo Rol',
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
                        vue.$refs.tblroles.loadRemoteData();
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
                        vue.$refs.tblroles.loadRemoteData();
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
                        vue.$refs.tblroles.loadRemoteData();
                    } else {
                        notify(response.message, "error");
                    }
                },
                error: function () {
                    notify(MESSAGES.errorComunicacion, "error");
                }
            });
        },
        nuevoRol: function () {
            let vue = this;
            vue.rolSuperior = {id: null};
            vue.rol = {id: null};
            vue.dataModalRol.title = "Nuevo Rol";
            vue.$refs.nuevorolmodal.open();
        },
        saveRol: function () {

            var vue = this;

            var valid = $('#formNuevoRol').parsley().validate();

            if (valid != true) {
                return;
            }

            vue.showLoader();

            $.ajax({
                method: 'POST',
                url: APP.url('seguridad/rol/save'),
                data: $('#formNuevoRol').serialize(),
                async :false,
                success: function (response) {

                    if (response.success) {

                        vue.hideLoader();
                        vue.$refs.nuevorolmodal.close();
                        vue.$refs.tblroles.loadRemoteData();

                    } else {
                        vue.hideLoader();
                        notify(response.message, 'error');
                    }


                }, error: function () {

                    vue.hideLoader();
                    notify(MESSAGES.errorComunicacion, "error");

                }
            });

        },
        editarRol: function (rol) {

            let vue = this;
            vue.rol = {id: null};
            vue.rolSuperior = {id: null};
            vue.dataModalRol.title = "Editar Rol";

            $.ajax({
                method: 'POST',
                url: APP.url('seguridad/rol/editar'),
                data: {id: rol.id},
                async :false,
                success: function (response) {
                    if (response.success) {

                        vue.rol = response.data;
                        vue.rolSuperior = response.data.rolSuperior;

                    } else {
                        notify(response.message, 'error');
                    }
                }, error: function () {
                    notify(MESSAGES.errorComunicacion, "error");
                }
            });

            vue.$refs.nuevorolmodal.open();

        },
        eliminarRol: function (rol) {

            let vue = this;
            swal({
                text: "¿Está seguro que desea eliminar el rol  " + rol.nombre + "?",
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
                    vue.deleteRol(rol);
                }
            });

        },
        deleteRol: function (rol) {

            let vue = this;

            $.ajax({
                method: 'POST',
                url: APP.url('seguridad/rol/delete'),
                data: {id: rol.id},
                success: function (response) {
                    if (response.success) {
                        notify(response.message, "info");
                        vue.$refs.tblroles.loadRemoteData();
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