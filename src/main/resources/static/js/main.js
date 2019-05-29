function getIndex(list, id) {
    for (var i = 0; i < list.length; i++ ) {
        if (list[i].id === id ) {
            return i;
        }
    }

    return -1;
}


var messageApi = Vue.resource('{/id}');

Vue.component('message-form', {
    props: ['messages', 'messageAttr'],
    data: function() {
        return {
            about:'',
            link: '',
            id: ''

        }
    },
    watch: {
        messageAttr: function(newVal, oldVal) {
            this.link = newVal.link;
            this.id = newVal.id;
            this.about=newVal.about;
        }
    },
    template:
        '<div>' +
        '<input type="text" style="width:200px" placeholder="Long Link" v-model="link" />' +
        '<input type="text"  style="width:200px" placeholder="Some notes..." v-model="about" />' +
        '<input type="button" value="Save" @click="save" />' +
        '</div>',
    methods: {
        save: function() {
            var message = { link: this.link,
                about:this.about};


            if (this.id) {
                messageApi.update({id: this.id}, message).then(result =>
                    result.json().then(data => {
                        var index = getIndex(this.messages, data.id);
                        this.messages.splice(index, 1, data);
                        this.link = '';
                        this.id = '';
                        this.about = '';
                    })
                )
            } else {
                messageApi.save({}, message).then(result =>
                    result.json().then(data => {
                        this.messages.push(data);
                        this.link = '';
                        this.about = ''

                    })
                )
            }
        }
    }
});

Vue.component('message-row', {
    props: ['message','about', 'editMethod', 'messages'],
    template: '<div>' +
        '<i>{{ message.id }}<span>&nbsp&nbsp</span></i> {{ message.link }}<b><span>&nbsp&nbsp</span>{{ message.about }}</b> ' +
        '<span style="position: absolute; right: 0;">' +
        '<input type="button" value="Edit" @click="edit" />' +
        '<input type="button" value="X" @click="del" />' +
        '<a v-bind:href="`/`+message.id">localhost:8080/message/{{message.id}}</a>'+
        '</span>' +

        '</div>',
    methods: {
        edit: function() {
            this.editMethod(this.message);
        },
        del: function() {
            messageApi.remove({id: this.message.id}).then(result => {
                if (result.ok) {
                    this.messages.splice(this.messages.indexOf(this.message), 1)
                }
            })
        }
    }
});

Vue.component('messages-list', {
    props: ['messages'],
    data: function() {
        return {
            message: null,
            about : null
        }
    },
    template:
        '<div style="position: relative; width: 800px;">' +
        '<message-form :messages="messages" :messageAttr="message"  />' +
        '<message-row v-for="message in messages" :key="message.id" :message="message"  :about="about"' +
        ':editMethod="editMethod" :messages="messages" />' +
        '</div>',
    methods: {
        editMethod: function(message,about) {
            this.message = message;


        }
    }
});

var app = new Vue({
    el: '#app',
    template:
        '<div>' +
        '<div v-if="!profile">Log in via <a href="/login">Google</a></div>' +
        '<div v-else>' +
        '<div>{{profile.name}}&nbsp;<a href="/logout">Log out </a></div>' +
        '<messages-list :messages="messages" />' +
        '</div>' +
        '</div>',
    data: {
        messages: frontendData.messages,
        profile: frontendData.profile
    },

});