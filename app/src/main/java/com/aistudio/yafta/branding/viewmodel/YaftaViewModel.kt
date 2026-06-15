package com.aistudio.yafta.branding.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.aistudio.yafta.branding.data.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class YaftaViewModel(private val dao: YaftaDao) : ViewModel() {

    // Reactive State Flows
    val allFinanceEntries: StateFlow<List<FinanceEntry>> = dao.getAllFinanceEntries()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allInventoryItems: StateFlow<List<InventoryItem>> = dao.getAllInventoryItems()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allProjects: StateFlow<List<ProductionProject>> = dao.getAllProjects()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allEstimates: StateFlow<List<ClientEstimate>> = dao.getAllEstimates()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allCrmContacts: StateFlow<List<CrmContact>> = dao.getAllCrmContacts()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allHrMembers: StateFlow<List<HrMember>> = dao.getAllHrMembers()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allHrTasks: StateFlow<List<HrTask>> = dao.getAllHrTasks()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allSettings: StateFlow<List<YaftaSetting>> = dao.getAllSettings()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Gemini AI States
    private val _aiResult = MutableStateFlow("")
    val aiResult: StateFlow<String> = _aiResult.asStateFlow()

    private val _aiLoading = MutableStateFlow(false)
    val aiLoading: StateFlow<Boolean> = _aiLoading.asStateFlow()

    init {
        // Pre-populate database if missing initial records
        viewModelScope.launch {
            allInventoryItems.take(1).collect { items ->
                if (items.isEmpty()) {
                    prepopulateDemoData()
                }
            }
        }
    }

    private suspend fun prepopulateDemoData() {
        // Mock inventory materials
        val initialInventory = listOf(
            InventoryItem(name = "Premium Acrylic Sheet 3mm (Red / Blue)", category = "Sheets", quantity = 22.0, unit = "Sheets", unitCost = 35.0, minThreshold = 5.0),
            InventoryItem(name = "Flex Banner Material (Glossy 440g)", category = "Rolls", quantity = 4.0, unit = "Rolls", unitCost = 120.0, minThreshold = 1.0),
            InventoryItem(name = "High-Brightness LED Module (Samsung 3-LED White)", category = "LEDs", quantity = 650.0, unit = "Pcs", unitCost = 0.45, minThreshold = 100.0),
            InventoryItem(name = "Heavy Duty Iron Angle (30x30x3mm)", category = "Structure", quantity = 32.0, unit = "Lts", unitCost = 8.50, minThreshold = 6.0),
            InventoryItem(name = "MeanWell Power Supply Output 12V 400W", category = "Electronics", quantity = 18.0, unit = "Pcs", unitCost = 28.0, minThreshold = 3.0),
            InventoryItem(name = "Neon Flex Tube Silicone 12V (Blue / Pink)", category = "LEDs", quantity = 15.0, unit = "Rolls", unitCost = 14.0, minThreshold = 2.0)
        )
        for (item in initialInventory) {
            dao.insertInventoryItem(item)
        }

        // Mock finance history
        val initialFinance = listOf(
            FinanceEntry(type = "INCOME", amount = 1450.0, description = "دفعة تعاقد لافتة أحرف مضيئة كافيه Espresso Glow", category = "Signage Orders"),
            FinanceEntry(type = "EXPENSE", amount = 350.0, description = "شراء رولات فينيل لاصق خارجي للمطبعة", category = "Raw Materials"),
            FinanceEntry(type = "INCOME", amount = 850.0, description = "مستخلص نهائي واجهة صيدلية TechVibe", category = "Backlit Sign"),
            FinanceEntry(type = "EXPENSE", amount = 120.0, description = "مصاريف غاز ولحام واستهلاكات حديد الورشة", category = "Welding & Structure"),
            FinanceEntry(type = "EXPENSE", amount = 250.0, description = "أجر يومية للفنيين وأوناش لتركيب بموقع ارتفاعات عالية", category = "Labor Cost")
        )
        for (entry in initialFinance) {
            dao.insertFinanceEntry(entry)
        }

        // Mock projects in different production states
        val initialProjects = listOf(
            ProductionProject(clientName = "Elesawy Cafe", phone = "+201012345678", signageText = "Espresso Glow", width = 3.5, height = 1.2, type = "3D Glowing Acrylic", totalCost = 2200.0, stage = "Lighting"),
            ProductionProject(clientName = "Alex Logistics Hub", phone = "+201201928374", signageText = "AX-DELIVERY", width = 12.0, height = 4.0, type = "Flex Billboard", totalCost = 4800.0, stage = "Fabrication"),
            ProductionProject(clientName = "Shine Dentistry", phone = "+201198765432", signageText = "Dr. Shady Clinic", width = 2.0, height = 0.8, type = "Neon Letters", totalCost = 1400.0, stage = "Installation"),
            ProductionProject(clientName = "Vintage Boutique", phone = "+201555543210", signageText = "COUTURE VIP", width = 1.5, height = 1.5, type = "Backlit Box", totalCost = 950.0, stage = "Draft")
        )
        for (project in initialProjects) {
            dao.insertProject(project)
        }
        
        // Mock estimates
        val initialEstimates = listOf(
            ClientEstimate(clientName = "Elite Gym Cairo", phone = "+201009182736", details = "Custom 4x1.5m 3D Glowing Steel Letters, Warm LED layout, 2x 300W PSU", estimatedPrice = 3100.0),
            ClientEstimate(clientName = "Sweet Treats Bakery", phone = "+201509871234", details = "Interior Neon Flex 'Baked with Love' on 1x1m Transparent Acrylic Base", estimatedPrice = 650.0)
        )
        for (estimate in initialEstimates) {
            dao.insertEstimate(estimate)
        }

        // Populating initial CRM Contacts
        val initialContacts = listOf(
            CrmContact(name = "أوراسكوم للإنشاءات", phone = "+201100223344", email = "procure@orascom.con", company = "Orascom", status = "Converted", notes = "تم توريد واجهات النيون والبنرات الخارجية ومتابعة التركيب"),
            CrmContact(name = "خير زمان ماركت", phone = "+201009988776", email = "marketing@kz-market.com", company = "Kheir Zaman Group", status = "Quoted", notes = "عرض سعر لتغطية واجهات الكلادينج لـ 3 فروع إقليمية جديدة قيد الدراسة"),
            CrmContact(name = "مطعم بيروت اللبناني", phone = "+201223344556", email = "beirut.food@gmail.com", company = "Beirut Food Co", status = "Followup", notes = "متابعة تصميم اللوحة الداخلية والاتفاق على التكلفة والتنزيلات"),
            CrmContact(name = "الدكتور أحمد صابر", phone = "+201555667788", email = "dr.saber.dentist@outlook.com", company = "Saber Dentistry", status = "Lead", notes = "طلب اتصال لمعرفة سعر المتر للوحة الحروف المضيئة النيون")
        )
        for (contact in initialContacts) {
            dao.insertCrmContact(contact)
        }

        // Populating HR Team Members
        val initialHrMembers = listOf(
            HrMember(name = "المهندس أسامة حمدي", role = "مدير الورشة والتصنيع (Workshop Director)", attendanceStatus = "Present", performanceScore = 95),
            HrMember(name = "عبد الرحمن خليل", role = "فني تشكيل ولحام هيدروليك (Welding Specialist)", attendanceStatus = "Present", performanceScore = 92),
            HrMember(name = "إياد حمزة", role = "فني ليد وتركيبات خارجية (LED & High-Site Tech)", attendanceStatus = "Present", performanceScore = 88),
            HrMember(name = "ندى عبد العزيز", role = "مصممة الجرافيك وتصميم اللوحات (Lead UI Designer)", attendanceStatus = "Present", performanceScore = 91)
        )
        for (member in initialHrMembers) {
            dao.insertHrMember(member)
        }

        // Populating HR Tasks
        val initialHrTasks = listOf(
            HrTask(title = "تصميم الهيكل ثلاثي الأبعاد لكافيه Espresso", assignedTo = "ندى عبد العزيز", status = "Completed", priority = "High", dueDate = "6-6-2026"),
            HrTask(title = "تلحيم حديد واجهة مستودع Alex Logistics", assignedTo = "عبد الرحمن خليل", status = "InProgress", priority = "High", dueDate = "7-6-2026"),
            HrTask(title = "تركيب واجهة لوحة النيون لعيادة التجميل", assignedTo = "إياد حمزة", status = "Pending", priority = "Medium", dueDate = "8-6-2026")
        )
        for (task in initialHrTasks) {
            dao.insertHrTask(task)
        }

        // Populating default settings
        val initialSettings = listOf(
            YaftaSetting(key = "company_name", value = "يافطة ميديا لإنتاج وتصميم الإعلانات المضيئة والكلادينج", description = "اسم المنشأة في عقود العمل وعروض الأسعار"),
            YaftaSetting(key = "tax_rate", value = "14.0", description = "قيمة ضريبة القيمة المضافة بالنسبة المئوية (%)"),
            YaftaSetting(key = "currency_code", value = "EGP", description = "رمز العملة الافتراضي للتقارير والبيانات"),
            YaftaSetting(key = "hq_address", value = "المنطقة الحرة، خلف النادي الأهلي، مدينة نصر، القاهرة", description = "عنوان المقر التجاري للشركة"),
            YaftaSetting(key = "backup_firebase_sync", value = "TRUE", description = "الربط السحابي والنسخ الاحتياطي التلقائي لقاعدة البيانات")
        )
        for (setting in initialSettings) {
            dao.insertSetting(setting)
        }
    }

    // Finance Actions
    fun addFinanceEntry(type: String, amount: Double, description: String, category: String) {
        viewModelScope.launch {
            dao.insertFinanceEntry(
                FinanceEntry(type = type, amount = amount, description = description, category = category)
            )
        }
    }

    fun deleteFinanceEntry(id: Int) {
        viewModelScope.launch {
            dao.deleteFinanceEntry(id)
        }
    }

    // Inventory Actions
    fun addInventoryItem(name: String, category: String, quantity: Double, unit: String, unitCost: Double, minThreshold: Double) {
        viewModelScope.launch {
            dao.insertInventoryItem(
                InventoryItem(name = name, category = category, quantity = quantity, unit = unit, unitCost = unitCost, minThreshold = minThreshold)
            )
        }
    }

    fun deleteInventoryItem(id: Int) {
        viewModelScope.launch {
            dao.deleteInventoryItem(id)
        }
    }

    // Production Project Actions
    fun addProject(clientName: String, phone: String, signageText: String, width: Double, height: Double, type: String, totalCost: Double) {
        viewModelScope.launch {
            dao.insertProject(
                ProductionProject(
                    clientName = clientName,
                    phone = phone,
                    signageText = signageText,
                    width = width,
                    height = height,
                    type = type,
                    totalCost = totalCost,
                    stage = "Draft"
                )
            )
        }
    }

    fun updateProjectStage(id: Int, stage: String) {
        viewModelScope.launch {
            dao.updateProjectStage(id, stage)
        }
    }

    fun deleteProject(id: Int) {
        viewModelScope.launch {
            dao.deleteProject(id)
        }
    }

    // Client Lead Estimate Actions
    fun addEstimate(clientName: String, phone: String, details: String, estimatedPrice: Double) {
        viewModelScope.launch {
            dao.insertEstimate(
                ClientEstimate(
                    clientName = clientName,
                    phone = phone,
                    details = details,
                    estimatedPrice = estimatedPrice
                )
            )
        }
    }

    fun deleteEstimate(id: Int) {
        viewModelScope.launch {
            dao.deleteEstimate(id)
        }
    }

    // CRM Actions
    fun addCrmContact(name: String, phone: String, email: String, company: String, status: String, notes: String) {
        viewModelScope.launch {
            dao.insertCrmContact(
                CrmContact(
                    name = name,
                    phone = phone,
                    email = email,
                    company = company,
                    status = status,
                    notes = notes
                )
            )
        }
    }

    fun deleteCrmContact(id: Int) {
        viewModelScope.launch {
            dao.deleteCrmContact(id)
        }
    }

    // HR Actions
    fun addHrMember(name: String, role: String, attendanceStatus: String, performanceScore: Int) {
        viewModelScope.launch {
            dao.insertHrMember(
                HrMember(
                    name = name,
                    role = role,
                    attendanceStatus = attendanceStatus,
                    performanceScore = performanceScore
                )
            )
        }
    }

    fun updateHrMemberStatus(id: Int, name: String, role: String, attendanceStatus: String, score: Int) {
        viewModelScope.launch {
            dao.insertHrMember(
                HrMember(
                    id = id,
                    name = name,
                    role = role,
                    attendanceStatus = attendanceStatus,
                    performanceScore = score
                )
            )
        }
    }

    fun deleteHrMember(id: Int) {
        viewModelScope.launch {
            dao.deleteHrMember(id)
        }
    }

    fun addHrTask(title: String, assignedTo: String, status: String, priority: String, dueDate: String) {
        viewModelScope.launch {
            dao.insertHrTask(
                HrTask(
                    title = title,
                    assignedTo = assignedTo,
                    status = status,
                    priority = priority,
                    dueDate = dueDate
                )
            )
        }
    }

    fun deleteHrTask(id: Int) {
        viewModelScope.launch {
            dao.deleteHrTask(id)
        }
    }

    // Settings Action
    fun updateSetting(key: String, value: String, description: String = "") {
        viewModelScope.launch {
            dao.insertSetting(
                YaftaSetting(key = key, value = value, description = description)
            )
        }
    }

    // AI Actions
    fun generateAiSuggestions(businessType: String, description: String, extraPrompt: String) {
        viewModelScope.launch {
            _aiLoading.value = true
            _aiResult.value = ""
            val result = GeminiService.generateSlogansAndDesigns(businessType, description, extraPrompt)
            _aiResult.value = result
            _aiLoading.value = false
        }
    }
}

// VM Factory to pass YaftaDao dependency securely
class YaftaViewModelFactory(private val dao: YaftaDao) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(YaftaViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return YaftaViewModel(dao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
