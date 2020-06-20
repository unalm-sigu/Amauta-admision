<template>
    <div class="panel-body">
        <button class="btn btn-primary btn-sm pull-right m-b-xs" v-on:click="openModal()"> + Agregar Producción </button>
        <h3 class="page-header"> Producción </h3> 
        <table class="table table-striped">
            <thead>
                <tr>
                    <th class="text-left">Título</th>
                    <th class="text-center">Tipo Producción</th>
                    <th class="text-center">Año Producción</th>
                    <th class="text-left">Fuente</th>
                    <th></th>
                </tr>
            </thead>
            <tbody v-for="(item, index) in $store.state.escalafon.produccionEscalafon" class="editor">
                <tr>
                    <td class="text-left v-middle">
                        <small class='h6'>{{ item.titulo  }}</small>
                    </td>
                    <td class="text-center">
                        {{item.tipo}}
                    </td>
                    <td class="text-center">
                        {{item.anioProduccion}}
                    </td>  
                    <td class="text-left">
                        {{item.tituloFuente}}
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
        <produccion-form ref="produccionFormVUE"></produccion-form>
        <confirmar-escalafon-form ref="confirmarEscalafonFormVUE"></confirmar-escalafon-form>
    </div>
</template>

<script>
    const ProduccionForm = httpVueLoader(package + '/produccion/ProduccionEscalafonForm.vue');
    const ConfirmarEscalafonForm = httpVueLoader(package + '/ConfirmarEscalafonForm.vue');
    module.exports = {
        components: {ProduccionForm, ConfirmarEscalafonForm},
        data() {
            return{
                rutaModulo: "/escalafon/produccion"
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
                let itemClone = Object.assign({}, item);
                this.$refs.produccionFormVUE.open(itemClone);
            },
            openModalConfirmacion(item) {
                let tipo = "ProduccionEsc";
                this.$refs.confirmarEscalafonFormVUE.open(item.id, tipo);
            },
            loadList() {
                let $vue = this;
                axios.post($vue.rutaModulo + "/loadListProduccionEscalafon", $vue.escalafon)
                        .then(function (response) {
                            if (response.data.success) {
                                $vue.setListProduccionEscalafon(response);
                            }
                        });
            },
            eliminar(item, index) {
                let $vue = this;
                bootbox.confirm({
                    message: MESSAGES.confirmDelete,
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
            setListProduccionEscalafon(item) {
                this.$store.commit('SET_LIST_PRODUCCION', item.data.data);
            }
        }
    };
</script>
