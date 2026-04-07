<template>
    <div>
        <header class="header b-b padder-lg">
            <div class="pull-right m-t-sm">
                <button class="btn btn-primary" v-on:click.prevent="nuevoGrupo">
                    Nuevo Grupo
                </button>
            </div>

            <h2> Grupos Horarios de Nivelación {{ciclo.descripcion}}</h2>
        </header>

        <section class="wrapper-lg">
            <section class="panel m-b-md">
                <section class="panel-body">

                    <raptor-table v-bind:url="gruposNivelacionURL" 
                                  v-bind:pagination="pagination"
                                  ref="raptorGrupo">
                        <template scope="props" >
                            <table class="table table-striped table-hover">
                                <thead class="panel panel-heading">
                                    <tr>
                                        <th class="v-middle text-center col-md-2">Grupo</th>
                                        <th class="v-middle text-center col-md-6">Tipo</th>
                                        <th class="v-middle text-center col-md-2">Horario {{ciclo.descripcion}}</th>
                                        <th class="v-middle text-center col-md-2">Horas</th>
                                        <th class=""></th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <tr v-for="item in props.data">
                                        <td class="v-middle text-center">
                                            <span class="block text-primary h4 m-t-xs m-b-xs">{{item.codigo}}</span>
                                        </td>

                                        <td class="v-middle text-center">
                                            {{item.tipo}}
                                        </td>
                                        <td class="v-middle text-center">
                                            <div v-on:click="configurarHorario(item)" class="pointer">
                                                <i class="fa fa-calendar fa-lg" v-bind:class="conHorarioClass(item)" aria-hidden="true"></i>
                                            </div>
                                        </td>
                                        <td class="v-middle text-center">
                                            <div v-bind:class="conHorarioClass(item)" class="block h4 m-t-xs m-b-xs">
                                                {{item.horas}}
                                            </div>
                                        </td>

                                        <td class="v-middle text-center">
                                            <div class="dropdown actions">
                                                <a class="dropdown-toggle" data-toggle="dropdown">
                                                    <i class="fa fa-cog"></i>
                                                </a>
                                                <ul class="dropdown-menu pull-right">
                                                    <li v-if="item.tipo != 'ZETA' " class="pointer"><a v-on:click="configurarHorario(item)">Configurar horario</a></li>
                                                    <li class="pointer"><a v-on:click="eliminar(item)">Eliminar horario</a></li>
                                                    <li class="pointer"><a v-on:click="editar(item)">Editar grupo</a></li>
                                                </ul>
                                            </div>
                                        </td>
                                    </tr>
                                </tbody>
                            </table>
                        </template>
                    </raptor-table>

                </section>
            </section>

        </section>

        <modal-add-grupo ref="modalAddGrupo"></modal-add-grupo>
        <modal-crear-horario ref="modalCrearHorario"></modal-crear-horario>
        <modal-confirm ref="modalConfirm"></modal-confirm>
    </div>

</template>
<script>
    const ModalConfirm = httpVueLoader('/app/_componentes/ModalConfirm.vue');
    const ModalAddGrupo = httpVueLoader('./ModalAddGrupo.vue');
    const ModalCrearHorario = httpVueLoader('./ModalCrearHorario.vue');

    module.exports = {
        components: {
            ModalAddGrupo, ModalConfirm, ModalCrearHorario
        },
        data() {
            return {
                idModalConfirmacion: "id-modal-confirmacion",
                ciclo: JSON.parse(cicloJson),
                gruposNivelacionURL: `/${rutaModulo}/list`,
                pagination: {'total-items': 0, 'items-per-page': 100, 'max-size': 3, 'boundary-link-numbers': true}
            };
        },
        methods: {

            nuevoGrupo() {
                this.$refs.modalAddGrupo.abrirModal(this.$refs.raptorGrupo);
            },
            conHorarioClass(item) {
                if (item.conHorario) {
                    return "text-success";
                }
                return "text-danger";
            },
            editar(item) {
                this.$refs.modalAddGrupo.editar(item, this.$refs.raptorGrupo);
            },
            configurarHorario(item) {
                if(item.tipo === 'ZETA') {
                    notify("No se configura el horario del grupo ZETA", "error");
                    return;
                }
                this.$refs.modalCrearHorario.open(item, this.$refs.raptorGrupo);
            },
            eliminar(item) {
                let config = VUE_MODAL.structConfirm({
                    id: this.idModalConfirmacion,
                    message: "¿Seguro que desea eliminar este grupo y su horario?",
                    okbtn: "Si, eliminar",
                    okclass: "btn-danger",
                    okaction: () => {
                        myUtils.axios(VUE_AXIOS.structModalClose({
                            url: `/${rutaModulo}/eliminarGrupo`,
                            modal: this.$refs.modalConfirm.getModal(),
                            raptor: this.$refs.raptorGrupo,
                            body: {id: item.id}
                        }));
                    }
                });

                this.$refs.modalConfirm.open(config);
            }
        }
    };

</script>