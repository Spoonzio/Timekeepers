# COMP 4911 Project

The repository has 3 main branches - **dev**, **test**, and **main**. These 3 branches are configured to 3 pods respectively - **development**, **test**, and **production**.

## Workflow

### Everyone
1. Clone the repository if you haven't yet.
    - `git clone https://gitlab.infoteach.ca/halimb/comp4911project.git` <br/>

### Developers
1. Before starting any work, ensure you're up-to-date with the latest changes on the `dev branch`.
    1. `git checkout dev`
    1. `git pull` <br/><br/>
1. Ensure you've switched (Ie. Checked out) to the `dev branch`.
	- `git checkout dev` <br/><br/>
1. Create a `dev sub-branch` from the `dev branch` for your implementation of the feature/fix/etc.
    - `git checkout -B dev_<team>_<feature>`
        - **Example:** `git checkout -B dev_frontend_login` <br/><br/>
1. Developer(s) can push their changes onto the `dev sub-branch` they've created.
    1. `git add .`
    1. `git commit -m "Your commit message here"`
    1. `git push origin dev_<team>_<feature>`
        - **Example:** `git push origin dev_frontend_login` <br/><br/>
1. Once the feature/fix/etc. has been fully implemented, push your changes onto your `dev sub-branch` and create a merge request from your `dev sub-branch` to the `dev branch`. Any merge requests to the `dev branch` can be approved by yourselves (Ie. Instantly merge your own merge requests). <br/><br/>
1. Once the changes have been merged from your `dev sub-branch` to the `dev branch`, delete your `dev sub-branch` from your local and remote repository.
    1. **Delete `dev sub-branch` from _local_ repository:** `git branch -D dev_<team>_<feature>`
        - **Example:** `git branch -D dev_frontend_login`
    1. **Delete `dev sub-branch` from _remote_ repository:** On repository webpage, go to Repository -> Branches -> Garbage Icon on the `dev sub-branch` you want to delete <br/><br/>
1. The developer(s) can message **Ben Halim** to reload the `development pod` with the updated changes from the `dev branch`. <br/><br/>
1. Once changes are applied to the `development pod`, the developer(s) can visit the `development pod` to quickly test that the changes they implemented are working as intended. <br/><br/>
1. Once the developer(s) have quickly ensured that the features are working as intended on the `development pod`, the developer(s) will update the QA team on the feature/fix/etc. they've implemented. To update the QA team, go to the **#merged-changes** discord chat and type in the following information.
    - **dev sub-branch:** _\<The dev sub-branch you worked on\>_
    - **Implementation:** _\<Description of what you implemented\>_
    - **Guideline:** _\<Rough guideline of what QA team should be testing for\><br/>_

### QA/Testers
1. After the QA/tester(s) obtains the information from the developer(s), the QA/tester(s) can then create a merge request from the `dev branch` to the `test branch`. Any merge requests to the `test branch` can be approved by yourselves (Ie. Instantly merge your own merge requests). <br/><br/>
1. After the `test branch` has been updated with the new feature/fix/etc., the QA/tester(s) should thoroughly test the `test branch` locally along with the `test pod`. 
    - The QA/tester(s) can message **Ben Halim** to reload the `test pod` with the updated changes from the `test branch`.
    - Note that the QA/tester(s) can also create their personal `test sub-branch` from the `test branch` if they want to test & edit a specific feature/fix/etc. Refer to Steps #1-6 under the **Developers** sub-section on how you should create, merge, and delete your `test sub-branch`. <br/><br/>
1. Once thorough testing is completed, the QA/tester(s) will create a merge request from the `test branch` to the `main branch`. A merge request to the `main branch` will require 2 approvals - one from the PM and one from a team lead. <br/><br/>
1. Once the merge request is approved, the QA/tester(s) making the merge request can merge to the `main branch`. <br/><br/>
1. Once the changes have been merged to the `main branch`, the QA/tester(s) can message **Ben Halim** to reload the `production pod` with the updated changes from the `main branch`.<br/>

## Links
- Development pod: http://development-halimb-labs.apps.okd4.infoteach.ca/ <br/>
- Test pod: http://test-halimb-labs.apps.okd4.infoteach.ca/ <br/>
- Production pod: http://production-halimb-labs.apps.okd4.infoteach.ca/ <br/>

## Notes
1. If you want to set an upstream (remote) branch to track a local branch so that you can just use `git push` instead of `git push origin <branch_name_here>` for a branch, run the following command.
	- `git branch -u origin/<remote-branch-name> <local-branch-name>`
		- Example: `git branch origin/dev_frontend_login dev_frontend_login` <br/><br/>
1. To check your branch statuses (Such as checking which remote branch is tracking which local branch), run `git branch -vv`. <br/><br/>
1. Contact information for steps in the **Workflow** section.
    - **Project manager:** James (Hang) Liu
        - **Discord:** James Liu#6443
        - **Email:** liuhang@kaka888.com
    - **Back-end team lead:** Reo Tamai
        - **Discord:** reotam27#5909
        - **Email:** reotamai5@gmail.com
    - **Front-end team lead:** Joonhyeong Kim
        - **Discord:** Joonior#4249
        - **Email:** kkjh6457@gmail.com
    - **QA/Test team lead:** Leon Wu
        - **Discord:** lein#0906
        - **Email:** 21leonwu@gmail.com
    - **System administrator:** Ben Halim
        - **Discord:** Lim#5346
        - **Email:** bhalim1994@gmail.com <br/><br/>
1. If you need another server to be made (Ex. Another pod to test a specific feature), please try not to spin up your own individual Cloud server or create your own OKD pod. Instead, just message me and I can configure a new pod for you right away 🙂. This is to ensure that the pod environments are standardized (Ie. The environments mirror each other) so that any problems that might pop up on the production pod will have already popped up on the other pods and will have already been resolved before they are pushed to production.<br/><br/>
1. Email notifications are sent to your email if your merge request is approved, someone assigned you to be a reviewer to a merge request, etc.
