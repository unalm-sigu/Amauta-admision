<template>
    <div>
        <div class="btn-group pull-right">
            <button v-bind:disabled="disabled" type="button" 
                    v-bind:class='classState()'
                    class="btn btn-sm dropdown-toggle" 
                    data-toggle="dropdown">
                {{estadoName}} <span class="caret"></span>
            </button>
            <ul class="dropdown-menu">
                <li>
                    <a v-on:click.prevent="changeState('ACEP')" href="#">ACEPTADO</a>
                </li>
                <li>
                    <a v-on:click.prevent="changeState('RCHZ')" href="#">RECHAZADO</a>
                </li>
            </ul>
        </div>
    </div>
</template>
<script>
    module.exports = {
        model: {
            prop: 'value',
            event: 'change'
        },
        props: {
            value: null,
            disabled:false,
        },
        components: {
        },
        data() {
            return {
                estados: {'ACEP': 'ACEPTADO', 'RCHZ': 'RECHAZADO', 'SOL': 'SOLICITADO', 'ANU': 'ANULADO'},
            };
        },
        computed: {
            estadoName: function () {
                let $vue = this;
                return $vue.estados[$vue.value]
            }
        },
        mounted: function () {
            let $vue = this;
        },
        methods: {
            changeState(state) {
                let $vue = this;
                $vue.$emit('change', state)
            },
            classState() {
                let $vue = this;
                if ($vue.value == 'ACEP') {
                    return 'btn-primary';
                }
                if ($vue.value == 'RCHZ') {
                    return 'btn-danger';
                }
                return 'btn-default';
            }
        }
    };
</script>