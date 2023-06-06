<template>

    <div class="root-steps">
        <div class="container-steps">
            <ul class="progressbar">
                <li v-for="item in steps"
                    v-on:click.prevent="selectStep(item)"
                    v-bind:class="styleStep(item)">
                    {{item.title}}
                </li>
            </ul>

        </div>
    </div>

</template>
<script>
    module.exports = {
        props: {
            steps: {}
        },
        methods: {
            styleStep(item) {
                if (item.locked) {
                    return "warning pointer";
                } else if (item.active) {
                    return "active pointer";
                }
                return "";
            },
            selectStep(item) {
                this.$parent.choiseStep(item);
            }
        }
    };
</script>
<style>
    .root-steps {
        margin-top: 30px;
        margin-bottom: 70px;
    }
    .container-steps {
        width: 100%;
        position: absolute;
        z-index: 1;
    }
    .progressbar {
        counter-reset: step;
    }
    .progressbar li {
        float: left;
        width: 14.28%;
        position: relative;
        text-align: center;
    }
    .progressbar li:before {
        content:counter(step);
        counter-increment: step;
        width: 30px;
        height: 30px;
        border: 2px solid #bebebe;
        display: block;
        margin: 0 auto 10px auto;
        border-radius: 50%;
        line-height: 27px;
        background: white;
        color: #bebebe;
        text-align: center;
        font-weight: bold;
    }
    .progressbar li:after {
        content: '';
        position: absolute;
        width: 100%;
        height: 3px;
        background: #979797;
        top: 15px;
        left: -50%;
        z-index: -1;
    }
    .progressbar li.active:before {
        border-color: #3aac5d;
        background: #3aac5d;
        color: white
    }
    .progressbar li.warning:before {
        border-color: #F6A40B;
        background: #F6A40B;
        color: white
    }
    .progressbar li.active:after {
        background: #3aac5d;
    }
    .progressbar li.active - li:after {
        background: #3aac5d;
    }
    .progressbar li.active - li:before {
        border-color: #3aac5d;
        background: #3aac5d;
        color: white
    }
    .progressbar li:first-child:after {
        content: none;
    }
    ul.progressbar {
        list-style-type: none;
        margin-bottom: 20px;
    }
</style>