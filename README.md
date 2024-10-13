# Pinterest

**Project Overview**
I built this project as a basic Pinterest clone using Jetpack Compose to practice UI development and layout design. The app features a login screen with social media sign-in options, a home screen that displays a grid of images, and a saved screen where users can search for their saved pins. This project is focused on the front-end aspect, specifically on creating visually appealing layouts, and does not currently include any backend logic or data storage.

**Setup Instructions**

1. Clone the repository:
Clone this project to your local machine using: git clone <[repository-url](https://github.com/FarhanFK2003/Pinterest)>

2. Open in Android Studio:
Open the cloned project in Android Studio (ensure you have the latest version installed).

3. Build and Run:
Once the project is loaded, build the project and run it on an Android emulator or physical device.

4. Dependencies:
Make sure you have the appropriate Jetpack Compose libraries set up in the build.gradle file.
Images like profile picture and grid images are placeholders. Replace these with actual resources in the res/drawable directory.

**Screens Designed and Their Purpose**

1. Signin Screen:
This is the first screen where users are prompted to sign in. They can enter their email address or choose to log in in via Facebook or Google.

2. Login Screen:
This is the second screen where users are prompted to if they want to login from their emails apart from Facebook or Google. They can enter their email address or choose to log in via Facebook or Google.

3. Home Screen:
This Screen displays a grid of images. The screen gives users an overview of various "pins" or images, organized in rows.

4. Saved Screen:
This Screen shows the user’s saved pins. There is also a search bar to search through the saved pins.

**Technical Challenges Faced**

1. Navigation Handling:
I initially planned to implement a bottom navigation bar with multiple pages, but I encountered issues with unresolved references for the BottomNavigation component. After some research, I decided to proceed with a simpler layout structure and exclude the navigation bar for now.

2. Dynamic Image Handling:
Since this project is front-end focused, handling dynamic images efficiently from resource files was crucial. I had to ensure that the grid layout could adapt well to different screen sizes while maintaining proper image alignment and spacing.

3. Alignment Issues:
Getting the alignment of elements, particularly on the login screen (e.g., centering the "Log in" text at the top), took some trial and error. Compose's flexible layout system made it easier to experiment with alignment, but it was a learning process to understand the best practices for composing UI elements.

**Future Plans**

1. Add Navigation Bar:
I will revisit the navigation system and ensure that users can seamlessly navigate between different sections of the app.

2. Implement User Interaction:
I plan to implement interaction functionality where users can search for images, click on them, and view them in more detail, similar to Pinterest’s pin functionality.

3. Responsive Design:
Another goal is to enhance responsiveness and design for different device sizes and orientations to ensure a consistent and appealing experience across various Android devices.


This project represents my journey in learning Jetpack Compose and building modern Android UIs. The focus is on structure and UI design, laying the foundation for future developments.
