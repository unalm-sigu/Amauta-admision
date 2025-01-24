<template>
    <modal-vik ref="modelChangeHorasDictado"
               v-bind="modelChangeHorasDictado"
               v-bind:okaction="saveChangeHorasDictado">
        <div slot="body">

            <h3 class="text-primary block m-b m-t">{{title}} {{ciclo.descripcion}}</h3>

            <form v-bind:id="form">
                <template v-if='visible'>
                    <div class="row">
                        <div class="col-md-9">
                            <span class="item-form-control item-form-gray text-primary">
                                {{cursoNiv.cursoCiclo.curso.codigo}} -
                                {{cursoNiv.cursoCiclo.curso.nombre}}
                                <br>
                                Sección: {{cursoNiv.codigo}}
                                &nbsp;&nbsp; | &nbsp;&nbsp;
                                {{cursoNiv.horasDictado}} horas
                                &nbsp;&nbsp; | &nbsp;&nbsp;
                                Del {{cursoNiv.fechaInicio}} al {{cursoNiv.fechaFin}}
                            </span>
                        </div>

                        <div class="col-md-3">
                            <span class="item-form-control item-form-gray text-primary">
                                Grupo horario
                                <br>
                                {{cursoNiv.grupoHoras.codigo}}
                            </span>
                        </div>
                    </div>

                    <div class="row m-t-md">
                        <div class="col-md-4">
                            <div class="form-group">
                                <label>Aula</label>
                                <span class="item-form-control item-form-gray text-primary">
                                    {{ cursoNiv.aula.codigo }}
                                    <span v-if="cursoNiv.aula.nombre"> - {{ cursoNiv.aula.nombre }}</span>
                                </span>
                            </div>
                        </div>

                        <template v-if="cursoNiv.aula">
                            <div class="col-md-4">
                                <label>Edificio / Pabellón</label>
                                <span class="item-form-control item-form-gray text-primary">
                                    {{ cursoNiv.aula.aulaSuperior.nombre }}
                                </span>
                            </div>

                            <div class="col-md-4">
                                <label>Capacidad / Aforo</label>
                                <span class="item-form-control item-form-gray text-primary">
                                    {{ cursoNiv.aula.capacidadAula }} /
                                    {{ cursoNiv.aula.aforo }}
                                </span>
                            </div>
                        </template>
                    </div>

                    <div class="row">
                        <div class="col-md-4">
                            <label>Horas Dictado</label>
                            <input v-model="cursoNiv.horasDictado" v-text="cursoNiv.horasDictado" type="text" class="form-control numeric" required="yes"/>
                        </div>
                       
                    </div>

                    <div class="row">
                        <div class="col-md-12  mt-3">
                            <div class="form-group">
                                <label>Indique el motivo del cambio</label>
                                <textarea v-model="cursoNiv.motivoCambio" class="form-control" rows="3"></textarea>
                            </div>
                        </div>
                    </div>

                </template>
            </form>
        </div>
    </modal-vik>
</template>

<script>

    module.exports = {

        data() {
            return {
                visible: false,
                curso: null,
                cursoNiv: null,
                raptor: null,
                gruposHoras: JSON.parse(gruposHorasJson),
                ciclo: JSON.parse(cicloJson),
                form: "id-form-change-horas-dictado",
                title: "Cambio de horas dictado",
                modelChangeHorasDictado: VUE_MODAL.structFormAjax({
                    id: "id-modal-change-horas-dictado",
                    okbtn: "Guardar cambio",
                    okclass: "btn-primary",
                    modalsize: "modal-lg"
                })
            };
        },

        mounted() {},
        computed: {
        },

        created() {
            this.searchAulaDebounce = debounce(this.searchAula, 800);
        },

        methods: {
            open(item, raptor) {
                var form = $("#" + this.form);
                form.parsley().destroy();

                this.raptor = raptor;
                this.cursoNiv = JSON.parse(JSON.stringify(item));
                this.visible = true;
                this.$refs.modelChangeHorasDictado.open();

                myUtils.activarNumeric();
            },

            saveChangeHorasDictado() {
                var form = $("#" + this.form);
                if (!form.parsley().validate()) {
                    notify("Debe completar los campos obligatorios", "error");
                    return;
                }

                const payload = {
                    id: this.cursoNiv.id,
                    horasDictado: this.cursoNiv.horasDictado,
                    motivoCambio: this.cursoNiv.motivoCambio
                };

                myUtils.axios(VUE_AXIOS.structModalClose({
                    url: `/${rutaModulo}/changeHorasDictado`,
                    modal: this.$refs.modelChangeHorasDictado,
                    raptor: this.raptor,
                    body: payload
                }));
            },

            getModal() {
                return this.$refs.modelChangeHorasDictado;
            },

            // metodos genericos
            getObjectId: myUtils.getObjectId,
            getObjectName: myUtils.getObjectName,
            commas: myUtils.commas
        }
    };
</script>