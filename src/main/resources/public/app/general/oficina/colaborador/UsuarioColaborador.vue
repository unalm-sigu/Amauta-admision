<template>
    <div>
        <modal-simple ref="modalUsuarioColaborador"
                      v-bind:okaction="saveUsuarioColaborador">
            <div slot="header">
                Usuario del colaborador para los servicios SIGU
            </div>
            <div slot="body">

                <form id="formDownloadLoteFoto" data-parsley-validate="true" method="POST">

                    <div v-if="colaborador">

                        Nombre <p class="bold">{{colaborador.persona.nombreCompleto}}</p>
                        Número de documento <p class="bold">{{colaborador.persona.numeroDocIdentidad}}</p>
                        Correo institucional <p class="bold">{{colaborador.persona.emailCompania}}</p>

                    </div>


                    Clave

                    <div class="form-group">

                        <input type="text" class="form-control" v-model="usuario.clave" required="true" />

                    </div> 



                    <input type="checkbox" class="" v-model="notificacion" />

                    Enviar notificación

                </form>

            </div>
        </modal-simple>
    </div>
</template>

<script>

    module.exports = {
        components: {
            ModalSimple: use("/_vue/modules/ModalSimple.vue"),
        },
        data() {
            return {
                colaborador: null,
                usuario: {},
                notificacion: null
            }
        },
        methods: {
            saveUsuarioColaborador() {

                let $vue = this;

                let urll = '/general/oficina/colaborador/' + $vue.colaborador.persona.id + '/usuario';

                if ($vue.notificacion) {

                    urll = '/general/oficina/colaborador/' + $vue.colaborador.persona.id + '/usuario/email';

                }

                axios_.post(urll, $vue.usuario)
                        .then(({data}) => {
                            $vue.$refs.modalUsuarioColaborador.close();
                            notify(data, "info");
                        }, () => {
                            $vue.$refs.modalUsuarioColaborador.stop();
                        });
            },
            open(colaborador) {
                let $vue = this;
                $vue.colaborador = {...colaborador}
                $vue.usuario = {}
                $vue.$refs.modalUsuarioColaborador.open();
            }
        }
    };
</script>