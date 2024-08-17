# StickyBottomSheet

Bottom sheet dialog fragment with sticky button on bottom of dialog.

[![Android CI](https://github.com/hoc081098/StickyBottomSheet/actions/workflows/android.yml/badge.svg)](https://github.com/hoc081098/StickyBottomSheet/actions/workflows/android.yml)
[![Hits](https://hits.seeyoufarm.com/api/count/incr/badge.svg?url=https%3A%2F%2Fgithub.com%2Fhoc081098%2FStickyBottomSheet&count_bg=%2379C83D&title_bg=%23555555&icon=&icon_color=%23E7E7E7&title=hits&edge_flat=false)](https://hits.seeyoufarm.com)

## I. Use two ComposeViews

- One `ComposeView` is used to display LazyColumn content.
- One `ComposeView` is used to display the bottom button.

<details>
    <summary>Click to expand video</summary>

https://github.com/user-attachments/assets/9eb3371e-0520-4e5b-b212-63c62e55e4f7

</details>

## II. Use View based

Full `View` based implementation.

<details>
    <summary>Click to expand video</summary>

https://github.com/user-attachments/assets/065ede9c-797e-4db4-af42-ab6c3a66d1b4

</details>

## III. Use one ComposeView

One `ComposeView` is used to display the whole content (LazyColumn content and the bottom button).
This version **has a problem**: the button update is not smooth (lags) when we expand/collapse the bottom sheet.

<details>
    <summary>Click to expand video</summary>

https://github.com/user-attachments/assets/d49eef7a-b754-4c8b-83e2-a1311b65d656

</details>