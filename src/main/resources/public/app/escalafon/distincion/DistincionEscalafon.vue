<template>
    <div class="panel-body">
        <button class="btn btn-primary btn-sm pull-right m-b-xs" v-on:click="openModal()"> + Agregar Distinción</button>
        <h3 class="page-header"> Distinción </h3>
        <table class="table table-striped">
            <thead>
                <tr>
                    <th class="col-md-4 text-left">Distinción</th>
                    <th class="col-md-5 text-left">Descripción</th>
                    <th class="col-md-2 text-left">País</th>
                    <th class="col-md-1 text-center">Fecha Premio</th>
                    <th></th>
                </tr>
            </thead>
            <tbody v-for="(item, index) in $store.state.escalafon.distincionEscalafon" class="editor">
                <tr>
                    <td class="text-left">
                        <small>  {{ item.titulo  }}</small>
                    </td>
                    <td class="text-left">
                        <small> {{item.descripcion}}</small>
                    </td>
                    <td class="text-left">
                        {{item.pais.nombre}}
                    </td>   
                    <td class="text-center">
                        {{item.fechaPremio}}
                    </td>   
                    <td class="text-center">
                        <div class="dropdown actions">
                            <a class="dropdown-toggle" data-toggle="dropdown"><i class="fa fa-cog"></i></a>
                            <ul class="dropdown-menu pull-right">
                                <li><a class="pointer" v-on:click="openModal(item)">Editar</a></li>
                                <li v-if="!item.confirmado" ><a class="pointer" v-on:click="openModalConfirmacion(item)">Confirmar</a></li>
                                <li><a class="pointer" v-on:click="eliminar(item, index)">Eliminar</a></li>
                            </ul>
                        </div>
                    </td>
                </tr>
            </tbody>
        </table>
        <distincion-form ref="distincionFormVUE"></distincion-form>
        <confirmar-escalafon-form ref="confirmarEscalafonFormVUE"></confirmar-escalafon-form>
    </div>
</template>

<script>
    const DistincionForm = httpVueLoader(package + '/distincion/DistincionEscalafonForm.vue');
    const ConfirmarEscalafonForm = httpVueLoader(package + '/ConfirmarEscalafonForm.vue');

    module.exports = {
        components: {DistincionForm, ConfirmarEscalafonForm},
        data() {
            return{
                rutaModulo: "/escalafon/distincion/"
            };
        },
        computed: {
            escalafon() {
                return this.$store.state.escalafon;
            }
        },
        mounted() {
        },
        methods: {
            openModal(item) {
                let itemClone = Object.assign({}, item)
                this.$refs.distincionFormVUE.open(itemClone);
            },
            openModalConfirmacion(item) {
                let tipo = "DistincionEsc";
                this.$refs.confirmarEscalafonFormVUE.open(item.id, tipo);
            },
            loadList() {
                let $vue = this;
                axios.post($vue.rutaModulo + "/loadListDistincionEscalafon", $vue.escalafon)
                        .then(function (response) {
                            if (response.data.success) {
                                $vue.setListDistincionEscalafon(response);
                            }
                        });
            },
            eliminar(item, index) {
                let $vue = this;
                bootbox.confirm({
                    message: Messages.confirmDelete,
                    buttons: {
                        confirm: {label: "Si, eliminar", className: "btn-danger"},
                        cancel: {label: "Cancelar", className: "btn-link"}
                    },
                    callback: function (result) {
                        if (result) {
                            axios.post($vue.rutaModulo + "/eliminar", item)
                                    .then(function (response) {
                                        if (response.data.success) {
                                            notify(response.data.message, "success");
                                            $vue.loadList();
                                        } else {
                                            notify(response.data.message, 'warning');
                                        }
                                    })
                                    .catch(function (error) {
                                        notify(error.errorComunicacion, "error");
                                    });
                        }
                    }
                });
            },
            setListDistincionEscalafon(item) {
                this.$store.commit('SET_LIST_DISTINCION', item.data.data);
            }
        }
    };
</script>
