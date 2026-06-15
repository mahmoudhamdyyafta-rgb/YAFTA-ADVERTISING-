package com.aistudio.yafta.branding.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aistudio.yafta.branding.data.HrMember
import com.aistudio.yafta.branding.data.HrTask
import com.aistudio.yafta.branding.ui.theme.*
import com.aistudio.yafta.branding.viewmodel.YaftaViewModel

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun HrScreen(viewModel: YaftaViewModel) {
    val members by viewModel.allHrMembers.collectAsState()
    val tasks by viewModel.allHrTasks.collectAsState()

    var activeSubTab by remember { mutableStateOf(0) } // 0 = Staff Directory, 1 = Tasks Board

    var showAddMemberDialog by remember { mutableStateOf(false) }
    var staffName by remember { mutableStateOf("") }
    var staffRole by remember { mutableStateOf("") }
    var staffScore by remember { mutableStateOf(90) }

    var showAddTaskDialog by remember { mutableStateOf(false) }
    var taskTitle by remember { mutableStateOf("") }
    var taskAssignedTo by remember { mutableStateOf("") }
    var taskPriority by remember { mutableStateOf("Medium") }
    var taskDueDate by remember { mutableStateOf("6-6-2026") }

    Scaffold(
        containerColor = Color.Transparent,
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    if (activeSubTab == 0) {
                        showAddMemberDialog = true
                    } else {
                        showAddTaskDialog = true
                    }
                },
                containerColor = CyberPrimary,
                contentColor = CyberBg,
                shape = RoundedCornerShape(14.dp)
            ) {
                Icon(if (activeSubTab == 0) Icons.Default.PersonAddAlt else Icons.Default.PlaylistAdd, "Add Staff Button")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header Screen
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "إدارة شؤون الموظفين والمهام • HR & Tasks",
                        color = CyberPrimary,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = if (activeSubTab == 0) "طاقم العمل والورشة" else "جدول المهام والتكليفات",
                        color = Color.White,
                        style = MaterialTheme.typography.titleLarge,
                        fontSize = 20.sp
                    )
                }

                // Mini stats badge
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(CyberSurface)
                        .border(1.dp, BorderColor, RoundedCornerShape(8.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = if (activeSubTab == 0) "طالع: ${members.filter { it.attendanceStatus == "Present" }.size}/${members.size}" else "مهام معلقة: ${tasks.count { it.status != "Completed" }}",
                        color = CyberTertiary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Dual Tab Header (Attendance List / Project Tasks)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(CyberSurface)
                    .border(1.dp, BorderColor, RoundedCornerShape(12.dp))
                    .padding(4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (activeSubTab == 0) CyberPrimary else Color.Transparent)
                        .clickable { activeSubTab = 0 }
                        .padding(vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "الحضور والموظفين",
                        color = if (activeSubTab == 0) CyberBg else Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                }

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (activeSubTab == 1) CyberPrimary else Color.Transparent)
                        .clickable { activeSubTab = 1 }
                        .padding(vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "التكليفات والمهام اليدوية",
                        color = if (activeSubTab == 1) CyberBg else Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                }
            }

            if (activeSubTab == 0) {
                // Directory Segment
                if (members.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .clip(RoundedCornerShape(16.dp))
                            .background(CyberSurface)
                            .border(1.dp, BorderColor, RoundedCornerShape(16.dp))
                            .padding(20.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.SupervisedUserCircle, "No Team", tint = TextSecondary, modifier = Modifier.size(48.dp))
                            Spacer(modifier = Modifier.height(12.dp))
                            Text("لا يوجد موظفين مسجلين", color = TextPrimary, fontWeight = FontWeight.Bold)
                            Text("أضف طاقم الفنيين والمهندسين لإدارة الحضور والموثوقية.", color = TextSecondary, fontSize = 12.sp)
                        }
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(members) { member ->
                            StaffMemberCard(
                                member = member,
                                onStatusChange = { newStatus ->
                                    viewModel.updateHrMemberStatus(
                                        id = member.id,
                                        name = member.name,
                                        role = member.role,
                                        attendanceStatus = newStatus,
                                        score = member.performanceScore
                                    )
                                },
                                onDelete = { viewModel.deleteHrMember(member.id) }
                            )
                        }
                    }
                }
            } else {
                // Tasks Segment
                if (tasks.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .clip(RoundedCornerShape(16.dp))
                            .background(CyberSurface)
                            .border(1.dp, BorderColor, RoundedCornerShape(16.dp))
                            .padding(20.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.Assignment, "No Tasks", tint = TextSecondary, modifier = Modifier.size(48.dp))
                            Spacer(modifier = Modifier.height(12.dp))
                            Text("سجل المهام خالي من التكليفات", color = TextPrimary, fontWeight = FontWeight.Bold)
                            Text("قم بإنشاء وتعيين تكليف لفني الورشة أو المصمم.", color = TextSecondary, fontSize = 12.sp)
                        }
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(tasks) { task ->
                            HrTaskCard(
                                task = task,
                                onToggleStatus = {
                                    val nextStatus = when(task.status) {
                                        "Pending" -> "InProgress"
                                        "InProgress" -> "Completed"
                                        else -> "Pending"
                                    }
                                    viewModel.deleteHrTask(task.id)
                                    viewModel.addHrTask(task.title, task.assignedTo, nextStatus, task.priority, task.dueDate)
                                },
                                onDelete = { viewModel.deleteHrTask(task.id) }
                            )
                        }
                    }
                }
            }
        }

        // Dialog: Add Staff Member
        if (showAddMemberDialog) {
            AlertDialog(
                onDismissRequest = { showAddMemberDialog = false },
                containerColor = CyberBg,
                modifier = Modifier.border(1.dp, BorderColor, RoundedCornerShape(20.dp)),
                title = { Text("إضافة مسؤول لفريق العمل", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp) },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        OutlinedTextField(
                            value = staffName,
                            onValueChange = { staffName = it },
                            label = { Text("اسم الموظف / الفني (Employee Name)") },
                            colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White, focusedBorderColor = CyberPrimary, unfocusedBorderColor = BorderColor)
                        )

                        OutlinedTextField(
                            value = staffRole,
                            onValueChange = { staffRole = it },
                            label = { Text("المسمى الوظيفي والدور (Role)") },
                            colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White, focusedBorderColor = CyberPrimary, unfocusedBorderColor = BorderColor)
                        )

                        Text("مؤشر التقييم والإنتاجية: $staffScore%", color = TextSecondary, fontSize = 12.sp)
                        Slider(
                            value = staffScore.toFloat(),
                            onValueChange = { staffScore = it.toInt() },
                            valueRange = 50f..100f,
                            colors = SliderDefaults.colors(thumbColor = CyberPrimary, activeTrackColor = CyberPrimary)
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            if (staffName.isNotBlank() && staffRole.isNotBlank()) {
                                viewModel.addHrMember(staffName, staffRole, "Present", staffScore)
                                staffName = ""
                                staffRole = ""
                                staffScore = 90
                                showAddMemberDialog = false
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = CyberPrimary)
                    ) {
                        Text("حفظ البياتات", color = CyberBg)
                    }
                },
                dismissButton = { TextButton(onClick = { showAddMemberDialog = false }) { Text("إلغاء", color = Color.White) } }
            )
        }

        // Dialog: Add Task
        if (showAddTaskDialog) {
            AlertDialog(
                onDismissRequest = { showAddTaskDialog = false },
                containerColor = CyberBg,
                modifier = Modifier.border(1.dp, BorderColor, RoundedCornerShape(20.dp)),
                title = { Text("إسناد مهمة جديدة للورشة", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp) },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        OutlinedTextField(
                            value = taskTitle,
                            onValueChange = { taskTitle = it },
                            label = { Text("توضيح المهمة المطلوبة (Task Decscription)") },
                            colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White, focusedBorderColor = CyberPrimary, unfocusedBorderColor = BorderColor)
                        )

                        OutlinedTextField(
                            value = taskAssignedTo,
                            onValueChange = { taskAssignedTo = it },
                            label = { Text("اسم المسند إليه المهمة (Assignee)") },
                            placeholder = { Text("مثال: عبد الرحمن خليل") },
                            colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White, focusedBorderColor = CyberPrimary, unfocusedBorderColor = BorderColor)
                        )

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text("أولوية الإنجاز:", color = TextSecondary, fontSize = 12.sp)
                            listOf("High", "Medium", "Low").forEach { priority ->
                                FilterChip(
                                    selected = taskPriority == priority,
                                    onClick = { taskPriority = priority },
                                    label = { Text(priority, fontSize = 11.sp) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = CyberSecondary,
                                        selectedLabelColor = CyberBg,
                                        containerColor = CyberSurface,
                                        labelColor = Color.White
                                    )
                                )
                            }
                        }

                        OutlinedTextField(
                            value = taskDueDate,
                            onValueChange = { taskDueDate = it },
                            label = { Text("تاريخ الإنجاز القياسي (Deadline)") },
                            colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White, focusedBorderColor = CyberPrimary, unfocusedBorderColor = BorderColor)
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            if (taskTitle.isNotBlank() && taskAssignedTo.isNotBlank()) {
                                viewModel.addHrTask(taskTitle, taskAssignedTo, "Pending", taskPriority, taskDueDate)
                                taskTitle = ""
                                taskAssignedTo = ""
                                taskPriority = "Medium"
                                taskDueDate = "6-6-2026"
                                showAddTaskDialog = false
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = CyberPrimary)
                    ) {
                        Text("حفظ وتعيين", color = CyberBg)
                    }
                },
                dismissButton = { TextButton(onClick = { showAddTaskDialog = false }) { Text("إلغاء", color = Color.White) } }
            )
        }
    }
}

@Composable
fun StaffMemberCard(
    member: HrMember,
    onStatusChange: (String) -> Unit,
    onDelete: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = CyberSurface),
        border = BorderStroke(1.dp, BorderColor),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(CyberSecondary.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Badge, "Role Icon", tint = CyberSecondary, modifier = Modifier.size(18.dp))
                    }
                    Column {
                        Text(member.name, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        Text(member.role, color = TextSecondary, fontSize = 11.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    }
                }

                // Score Badge
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(CyberTertiary.copy(alpha = 0.15f))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text("التقييم: ${member.performanceScore}%", color = CyberTertiary, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }
            }

            Divider(color = BorderColor.copy(alpha = 0.4f), thickness = 1.dp)

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text("دفتر الدوام اليومي:", color = TextSecondary, fontSize = 11.sp)
                    listOf("Present", "Absent", "On Leave").forEach { status ->
                        val label = when(status) {
                            "Present" -> "حاضر"
                            "Absent" -> "غائب"
                            else -> "إجازة"
                        }
                        FilterChip(
                            selected = member.attendanceStatus == status,
                            onClick = { onStatusChange(status) },
                            label = { Text(label, fontSize = 10.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = when(status) {
                                    "Present" -> AccentSuccess
                                    "Absent" -> Color.Red
                                    else -> AccentWarning
                                },
                                selectedLabelColor = CyberBg,
                                containerColor = CyberSurface,
                                labelColor = Color.White
                            ),
                            modifier = Modifier.padding(horizontal = 2.dp)
                        )
                    }
                }

                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, "Delete Employee", tint = Color.Red.copy(alpha = 0.7f), modifier = Modifier.size(16.dp))
                }
            }
        }
    }
}

@Composable
fun HrTaskCard(
    task: HrTask,
    onToggleStatus: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = CyberSurface.copy(alpha = 0.8f)),
        border = BorderStroke(1.dp, BorderColor.copy(alpha = 0.6f)),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    IconButton(onClick = onToggleStatus) {
                        Icon(
                            imageVector = when(task.status) {
                                "Completed" -> Icons.Default.CheckCircle
                                "InProgress" -> Icons.Default.Cached
                                else -> Icons.Default.RadioButtonUnchecked
                            },
                            contentDescription = "Status Clicker",
                            tint = when(task.status) {
                                "Completed" -> AccentSuccess
                                "InProgress" -> CyberSecondary
                                else -> Color.LightGray
                            },
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    Column {
                        Text(
                            text = task.title,
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = "المكلف: " + task.assignedTo,
                            color = TextSecondary,
                            fontSize = 11.sp
                        )
                    }
                }

                // Priority Badge
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(
                            when(task.priority) {
                                "High" -> Color.Red.copy(alpha = 0.15f)
                                "Medium" -> AccentWarning.copy(alpha = 0.15f)
                                else -> AccentSuccess.copy(alpha = 0.15f)
                            }
                        )
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = when(task.priority) {
                            "High" -> "عاجل"
                            "Medium" -> "عادي"
                            else -> "منخفض"
                        },
                        color = when(task.priority) {
                            "High" -> Color.Red
                            "Medium" -> AccentWarning
                            else -> AccentSuccess
                        },
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "موعد التسليم المتوقع: ${task.dueDate}",
                    color = TextPrimary,
                    fontSize = 11.sp
                )

                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, "Delete Task", tint = Color.LightGray, modifier = Modifier.size(16.dp))
                }
            }
        }
    }
}
