// 在vm内部访问属性方法用this
		var vm=new Vue({
			el:'#app',
			data(){
				return{
					username:'',
				}
			},
			created(){
				axios.post('/property/check2.do')
	    		  .then(function (response) {
	    		    var res=response.data;
	    		    if(res=='no'){
	    		    	window.location.href = "./index.html"
	    		    }else{
	    		    	vm.username=res;
	    		    }
	    		  })
			},
			methods:{
				logout(){
					axios.post('/property/logout.do')
		    		  .then(function (response) {
		    		    var res=response.data;
		    		    if(res=="ok"){
		    		    	window.location.href = "./index.html"
		    		    }
		    		  })
				},
				myinfo(){
					window.location.href = "./user_myinfo.html"
				},
				mycar(){
					window.location.href = "./user_mycar.html"
				},
				feenotdo(){
					window.location.href = "./user_feenotdo.html"
				},
				feedo(){
					window.location.href = "./user_feedo.html"
				},
				fix(){
					window.location.href = "./user_fix.html"
				},
			
				
				
			}
		})