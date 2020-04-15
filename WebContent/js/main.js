// 在vm内部访问属性方法用this
		var vm=new Vue({
			el:'#app',
			data(){
				return{
					username:'',
				}
			},
			created(){
				axios.post('/property/check.do')
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
				houseInfo(){
					window.location.href = "./admin_houseInfo.html"
				},
				houseInOut(){
					window.location.href = "./admin_houseInOut.html"
				},
				carInfo(){
					window.location.href = "./admin_carInfo.html"
				},
				carInvited(){
					window.location.href = "./admin_carInvited.html"
				},
				money(){
					var fee1=document.getElementById("feenotdo");
					var fee2=document.getElementById("feedo");
					if(fee1.style.display=="none"){
						fee1.style.display="block";
						fee2.style.display="block";
					}else if(fee1.style.display=="block"){
						fee1.style.display="none";
						fee2.style.display="none";
					}
				},
				fee1(){
					window.location.href = "./admin_money.html"
				},
				fee2(){
					window.location.href = "./admin_money2.html"
				},
				fix(){
					window.location.href = "./admin_fix.html"
				},
				
			}
		})