<template>
    <modal-vik ref="modalAddGrupo"
               v-bind="modalAddGrupo"
               v-bind:okaction="saveGrupo">
        <div slot="body">

            <h4 class="text-primary block m-b-lg">{{title}}</h4>

            <form v-bind:id="form" data-parsley-validate="">
                <template>

                    <div class="form-group">
                        <label>Código</label>
                        <input class="form-control" 
                               v-model="grupo.codigo" 
                               required="true"
                               maxlength="10"
                               pattern="^[A-Z][A-Z0-9\/-]*$"
                               data-parsley-pattern-message="Debe empezar con letra y solo permite letras, números, guión y slash"
                               v-on:keypress="preventSpace"
                               v-on:input="forceUpperCase"
                               style="text-transform: uppercase;"
                               />
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
                form: "id-form-grupo-nivelacion",
                title: "",
                grupo: {id: null, codigo: ''},
                raptor: null,
                modalAddGrupo: VUE_MODAL.structFormAjax({
                    id: "id-modal-add-grupo",
                    okbtn: "Guardar",
                    okclass: "btn-primary"
                })
            };
        },
        methods: {

            abrirModal(raptor) {
                var form = $("#" + this.form);
                form.parsley().destroy();

                this.grupo = {id: null, codigo: ''};
                this.title = "Nuevo Grupo Nivelación";
                this.$refs.modalAddGrupo.open();
                this.raptor = raptor;

            },
            editar(item, raptor) {
                var form = $("#" + this.form);
                form.parsley().destroy();

                this.grupo = JSON.parse(JSON.stringify(item));
                this.title = "Editar Grupo Nivelación";
                this.raptor = raptor;
                this.$refs.modalAddGrupo.open();
                this.$refs.modalAddGrupo.okbtn = "Actualizar";
            },

            saveGrupo() {
                console.log("grupo", this.grupo)
                var form = $("#" + this.form);
                if (!form.parsley().validate()) {
                    return;
                }

                myUtils.axios(VUE_AXIOS.structModalClose({
                    url: `/${rutaModulo}/saveGrupo`,
                    modal: this.$refs.modalAddGrupo,
                    raptor: this.raptor,
                    body: this.grupo
                }));
            },
            preventSpace(event) {
               if (event.which === 32) {
                   event.preventDefault();
               }
            },
            forceUpperCase(event) {
                const valor = event.target.value.toUpperCase();
                this.grupo.codigo = valor;
                this.$forceUpdate();
            }

        }
    };
</script>