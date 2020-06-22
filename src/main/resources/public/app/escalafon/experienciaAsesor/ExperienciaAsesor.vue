<template>
    <div class="panel-body">
        <button class="btn btn-primary btn-sm pull-right m-b-xs" v-on:click="openModal()"> + Agregar Experiencia Asesor</button>
        <h3 class="page-header"> Experiencia como Asesor de Tesis </h3>
        <table class="table table-striped">
            <thead>
                <tr>
                    <th>Universidad</th>
                    <th>Tesis</th>
                    <th>Tesista</th>
                    <th>Repositorio</th>
                    <th>Fecha Aceptado</th>
                    <th></th>
                </tr>
            </thead>
            <tbody v-for="(item, index) in $store.state.escalafon.experienciaAsesor" class="editor">
                <tr>
                    <td>
                        {{ item.universidad.nombre }}
                    </td>  
                    <td>
                        {{item.tipoTesisEnum.descripcion}}
                    </td>
                    <td>
                        {{item.tesista}}
                    </td>
                    <td>
                        <a class="pointer" target="_blank" v-bind:href='item.urlRepositorio'>{{item.urlRepositorio}} </a>
                    </td>
                    <td>
                        {{item.fechaAceptacion}}
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
        <experiencia-asesor-form ref="experienciaAsesorFormVUE"></experiencia-asesor-form>
        <confirmar-escalafon-form ref="confirmarEscalafonFormVUE"></confirmar-escalafon-form>
    </div>
</template>

<script>
    const ExperienciaAsesorForm = httpVueLoader(package + '/experienciaAsesor/ExperienciaAsesorForm.vue');
    const ConfirmarEscalafonForm = httpVueLoader(package + '/ConfirmarEscalafonForm.vue');

    module.exports = {
        components: {ExperienciaAsesorForm, ConfirmarEscalafonForm},
        data() {
            return{
                rutaModulo: "/escalafon/experienciaAsesor"
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
                this.$refs.experienciaAsesorFormVUE.open(itemClone);
            },
            openModalConfirmacion(item) {
                let tipo = "ExperienciaAsesorEsc";
                this.$refs.confirmarEscalafonFormVUE.open(item.id, tipo);
            },
            loadList() {
                let $vue = this;
                axios.post($vue.rutaModulo + "/loadListExperienciaAsesor", $vue.escalafon)
                        .then(function (response) {
                            if (response.data.success) {
                                $vue.setListExperienciaAsesor(response);
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
            setListExperienciaAsesor(item) {
                this.$store.commit('SET_LIST_EXP_ASESOR', item.data.data);
            }
        }
    };
</script>
