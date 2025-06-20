var change_list_button_el=document.querySelector(".change_list");
var dist_list_button_el=document.querySelector(".dist_list");
document.addEventListener("click",function(e){
    if(e.target.classList.contains("change_list")){
        
        
        change_list_button_el.classList.add("on");
        dist_list_button_el.classList.remove("on");
        
    }else if(e.target.classList.contains("dist_list")){
        change_list_button_el.classList.remove("on");
        dist_list_button_el.classList.add("on");
    }
    
})



document.getElementById('datatable_search').addEventListener('input', function () {
    $('#example').DataTable().search(this.value).draw();
  });
