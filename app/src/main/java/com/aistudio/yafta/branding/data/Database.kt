package com.aistudio.yafta.branding.data

import android.content.Context
import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "finance_entries")
data class FinanceEntry(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val type: String, // INCOME or EXPENSE
    val amount: Double,
    val description: String,
    val category: String,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "inventory_items")
data class InventoryItem(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val category: String,
    val quantity: Double,
    val unit: String,
    val unitCost: Double,
    val minThreshold: Double
)

@Entity(tableName = "production_projects")
data class ProductionProject(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val clientName: String,
    val phone: String,
    val signageText: String,
    val width: Double,
    val height: Double,
    val type: String, // e.g. "3D Glowing", "Flex Billboard", "Neon Lettering", "Light Box"
    val totalCost: Double,
    val stage: String, // "Draft", "Fabrication", "Lighting", "Assembly", "Delivery", "Installation"
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "client_estimates")
data class ClientEstimate(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val clientName: String,
    val phone: String,
    val details: String,
    val estimatedPrice: Double,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "crm_contacts")
data class CrmContact(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val phone: String,
    val email: String,
    val company: String,
    val status: String, // Lead, Quoted, Followup, Converted, Lost
    val notes: String = "",
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "hr_members")
data class HrMember(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val role: String,
    val attendanceStatus: String, // Present, Absent, On Leave
    val attendanceTime: Long = System.currentTimeMillis(),
    val performanceScore: Int = 85
)

@Entity(tableName = "hr_tasks")
data class HrTask(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val assignedTo: String,
    val status: String, // Pending, InProgress, Completed
    val priority: String, // High, Medium, Low
    val dueDate: String = ""
)

@Entity(tableName = "yafta_settings")
data class YaftaSetting(
    @PrimaryKey val key: String,
    val value: String,
    val description: String = ""
)

@Dao
interface YaftaDao {
    // Finance Queries
    @Query("SELECT * FROM finance_entries ORDER BY timestamp DESC")
    fun getAllFinanceEntries(): Flow<List<FinanceEntry>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFinanceEntry(entry: FinanceEntry)

    @Query("DELETE FROM finance_entries WHERE id = :id")
    suspend fun deleteFinanceEntry(id: Int)

    // Inventory Queries
    @Query("SELECT * FROM inventory_items ORDER BY name ASC")
    fun getAllInventoryItems(): Flow<List<InventoryItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertInventoryItem(item: InventoryItem)

    @Query("DELETE FROM inventory_items WHERE id = :id")
    suspend fun deleteInventoryItem(id: Int)

    // Production queries
    @Query("SELECT * FROM production_projects ORDER BY timestamp DESC")
    fun getAllProjects(): Flow<List<ProductionProject>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProject(project: ProductionProject)

    @Query("UPDATE production_projects SET stage = :stage WHERE id = :id")
    suspend fun updateProjectStage(id: Int, stage: String)

    @Query("DELETE FROM production_projects WHERE id = :id")
    suspend fun deleteProject(id: Int)

    // Lead Estimate Queries
    @Query("SELECT * FROM client_estimates ORDER BY timestamp DESC")
    fun getAllEstimates(): Flow<List<ClientEstimate>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEstimate(estimate: ClientEstimate)

    @Query("DELETE FROM client_estimates WHERE id = :id")
    suspend fun deleteEstimate(id: Int)

    // CRM Queries
    @Query("SELECT * FROM crm_contacts ORDER BY name ASC")
    fun getAllCrmContacts(): Flow<List<CrmContact>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCrmContact(contact: CrmContact)

    @Query("DELETE FROM crm_contacts WHERE id = :id")
    suspend fun deleteCrmContact(id: Int)

    // HR Queries
    @Query("SELECT * FROM hr_members ORDER BY name ASC")
    fun getAllHrMembers(): Flow<List<HrMember>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHrMember(member: HrMember)

    @Query("DELETE FROM hr_members WHERE id = :id")
    suspend fun deleteHrMember(id: Int)

    @Query("SELECT * FROM hr_tasks ORDER BY id DESC")
    fun getAllHrTasks(): Flow<List<HrTask>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHrTask(task: HrTask)

    @Query("DELETE FROM hr_tasks WHERE id = :id")
    suspend fun deleteHrTask(id: Int)

    // Settings Queries
    @Query("SELECT * FROM yafta_settings")
    fun getAllSettings(): Flow<List<YaftaSetting>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSetting(setting: YaftaSetting)
}

@Database(
    entities = [
        FinanceEntry::class,
        InventoryItem::class,
        ProductionProject::class,
        ClientEstimate::class,
        CrmContact::class,
        HrMember::class,
        HrTask::class,
        YaftaSetting::class
    ],
    version = 2,
    exportSchema = false
)
abstract class YaftaDatabase : RoomDatabase() {
    abstract fun yaftaDao(): YaftaDao

    companion object {
        @Volatile
        private var INSTANCE: YaftaDatabase? = null

        fun getDatabase(context: Context): YaftaDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    YaftaDatabase::class.java,
                    "yafta_database"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
