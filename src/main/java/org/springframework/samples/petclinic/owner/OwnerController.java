package org.springframework.samples.petclinic.owner;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import jakarta.validation.Valid;

@Controller
class OwnerController {

	private static final String VIEWS_OWNER_CREATE_OR_UPDATE_FORM = "owners/createOrUpdateOwnerForm";

	private final OwnerRepository owners;

	//コントローラークラスでコンストラクタを作成する意図がわからない。フィールドを定数にできるため？
	public OwnerController(OwnerRepository clinicService) {
		this.owners = clinicService;
	}

	//idパラメーターのみバインドを拒否？(何をしているかが全く想像できない)
	@InitBinder
	public void setAllowedFields(WebDataBinder dataBinder) {
		dataBinder.setDisallowedFields("id");
	}

	//ownerクラスのモデリング。引数のOwnerクラスはownerとしてthymeleaf側で認識
	@ModelAttribute("owner")
	public Owner findOwner(@PathVariable(name = "ownerId", required = false) Integer ownerId) {
		return ownerId == null ? new Owner() : this.owners.findById(ownerId);
	}

	@GetMapping("/owners/new")
	public String initCreationForm() {
		//ownerの新規インスタンスを作成してmapにputしている意図がわからない。なくても問題ないのでは？
		return VIEWS_OWNER_CREATE_OR_UPDATE_FORM;
	}

	@PostMapping("/owners/new")
	public String processCreationForm(@Valid Owner owner, BindingResult result) {
		if (result.hasErrors()) {
			return VIEWS_OWNER_CREATE_OR_UPDATE_FORM;
		}

		this.owners.save(owner);
		return "redirect:/owners/" + owner.getId();
	}

	@GetMapping("/owners/find")
	public String initFindForm() {
		return "owners/findOwners";
	}

	@GetMapping("/owners")
	public String processFindForm(@RequestParam(defaultValue = "1") int page, Owner owner, BindingResult result,
			Model model) {
		if (owner.getLastName() == null) {
			owner.setLastName("");//lastnameがnullで入ってきた場合、空文字として登録
		}

		Page<Owner> ownerResults = findPaginatedForOwnersLastName(page, owner.getLastName());
		if (ownerResults.isEmpty()) {
			//検索対象が存在しなかったらバリデーションメッセージを表示させる。
			result.rejectValue("lastName", "notFound", "not found");
			return "owners/findOwners";
		}

		if (ownerResults.getTotalElements() == 1) {
			//検索結果が一見の場合、該当者の詳細ページに移行
			owner = ownerResults.iterator().next();//次の要素を取得
			return "redirect:/owners/" + owner.getId();
		}

		return addPaginationModel(page, model, ownerResults);
	}

	private String addPaginationModel(int page, Model model, Page<Owner> paginated) {
		model.addAttribute("currentPage", page);
		List<Owner> listOwners = paginated.getContent();//PageクラスをListとして返す
		model.addAttribute("currentPage", page);
		model.addAttribute("totalPages", paginated.getTotalPages());//総ページ数
		model.addAttribute("totalItems", paginated.getTotalElements());//総要素数
		model.addAttribute("listOwners", listOwners);
		return "owners/ownersList";
	}

	private Page<Owner> findPaginatedForOwnersLastName(int page, String lastname) {
		int pageSize = 5;//１ページに表示する要素数
		Pageable pageable = PageRequest.of(page - 1, pageSize);//ページ数と要素数指定
		return owners.findByLastName(lastname, pageable);
	}

	@GetMapping("/owners/{ownerId}/edit")
	public String initUpdateOwnerForm(@PathVariable("ownerId") int ownerId, Model model) {
		Owner owner = this.owners.findById(ownerId);
		model.addAttribute(owner);
		return VIEWS_OWNER_CREATE_OR_UPDATE_FORM;
	}

	@PostMapping("/owners/{ownerId}/edit")
	public String processUpdateOwnerForm(@Valid Owner owner, BindingResult result,
			@PathVariable("ownerId") int ownerId) {
		if (result.hasErrors()) {
			return VIEWS_OWNER_CREATE_OR_UPDATE_FORM;
		}

		owner.setId(ownerId);
		this.owners.save(owner);
		return "redirect:/owners/{ownerId}";
	}

	@GetMapping("/owners/{ownerId}")
	//なぜここはStringではなくMdelAndView？
	public ModelAndView showOwner(@PathVariable("ownerId") int ownerId) {
		ModelAndView mav = new ModelAndView("owners/ownerDetails");
		Owner owner = this.owners.findById(ownerId);
		mav.addObject(owner);
		return mav;
	}

}
