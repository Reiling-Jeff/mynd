# mynd - Mental Support App (In Development)

![Status](https://img.shields.io/badge/status-in%20progress-yellow)

**mynd** is an Android application designed to offer a simple and intuitive way to manage your mind (which is where the name comes from: "my mind" -> "mynd"). The project is in its very early stages, with many of the core features still in development.

---

## 📌 Vision & Planned Features

Although the app is just getting started, the vision is to create a modern, powerful, and individual tool for personal reflection and organization. The following features are planned:

*   **Journal:** An easy-to-use mood selector in the calendar view.
*   **Calendar Features:** Highlighting days where you have already created one or more entries.
*   **Personalization:** Easily personalize the app. You will be able to select and deselect features, so you never have to see something you don't need.
*   **Backup & Restore:** A function to back up and restore the entire note database.
*   **Motivation:** Mynd will motivate you to start your journal on a daily basis (for example, with a streak feature).
*   **Medication:** A simple reminder to take your meds.
*   **More Languages:** For now, only German is supported. This will, of course, change in the future.

---

## 🛠️ Current Development Status (Work in Progress)

Based on the existing codebase, the following basic functionalities have been implemented:

*   **📝 Note Management:** Create, edit, and delete text-based notes.
*   **📅 Calendar Integration:** A calendar view to see and create notes.
*   **🔒 Note Lock:** Protect private notes using biometric authentication (fingerprint/face ID).
*   **🎨 Modern UI:** A clean, uncluttered user interface that focuses on the essentials.
*   **🚀 Efficient Performance:** Optimized database access and UI updates for a smooth user experience.

**Please keep in mynd (lol) that all features will probably change in the future. Nothing is final, and everything could be deleted or remade before version 1.0 is released.**

---

## 🚀 Technical Details & Architecture

The project is being built using modern Android development technologies and principles:

*   **Language:** [Kotlin](https://kotlinlang.org/)
*   **Architecture:** The goal is to implement MVVM (Model-View-ViewModel). Currently, the logic is still tightly coupled with `MainActivity`.
*   **Asynchronous Programming:** [Kotlin Coroutines](https://kotlinlang.org/docs/coroutines-overview.html) are used for database access and other background tasks.
*   **Database:** [Room Persistence Library](https://developer.android.com/training/data-storage/room) for the local SQLite database.
*   **UI Components:**
    *   [RecyclerView](https://developer.android.com/guide/topics/ui/layout/recyclerview) for displaying lists efficiently.
    *   [Material Components](https://material.io/develop/android) for UI elements like the `FloatingActionButton`.

---

## 🔧 How to Build the Project

1.  Clone this repository to your local machine.
2.  Open the project using the latest stable version of [Android Studio](https://developer.android.com/studio).
3.  Allow Gradle to download dependencies and sync the project.
4.  Select the `app` module and run it on an emulator or a physical device.

**Of course, you will also be able to easily download the official release versions.**

---

## 🚧 Known TODOs & Next Steps

The current code contains several areas marked for improvement, which serve as the immediate next steps:

*   **Introduce View Binding:** Replace `findViewById` with [View Binding](https.android.com/topic/libraries/view-binding) to improve code safety and readability.
*   **Implement DiffUtil:** Replace `notifyDataSetChanged()` in the `NoteAdapter` with a `ListAdapter` and `DiffUtil` to optimize UI updates and performance.
*   **Use a ViewModel:** Move the business logic from `MainActivity` into a `ViewModel` to achieve a cleaner separation of concerns and handle configuration changes gracefully.
*   **Dependency Injection:** Consider using a framework like Hilt or Koin to provide dependencies (e.g., the database instance).
*   **Optimize Drawing:** Avoid creating `Paint` and `RectF` objects repeatedly inside `onChildDraw` to improve drawing performance during the swipe gesture.
*   **Implement Core Features:**
    *   Finish the `NoteActivity` (saving, editing).
    *   Add functionality to the `CalendarActivity`.
    *   Implement the settings and note-locking logic.

