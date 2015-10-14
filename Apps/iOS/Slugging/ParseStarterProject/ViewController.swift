/**
* Copyright (c) 2015-present, Parse, LLC.
* All rights reserved.
*
* This source code is licensed under the BSD-style license found in the
* LICENSE file in the root directory of this source tree. An additional grant
* of patent rights can be found in the PATENTS file in the same directory.
*/

import UIKit
import Parse

class ViewController: UIViewController {
    
    
    @IBOutlet weak var emailAddress: UITextField!
    @IBOutlet weak var password: UITextField!
    
    @IBAction func signUp(sender: AnyObject) {
        let user = PFUser()
        user.username = emailAddress.text
        user.password = password.text
        user.email = emailAddress.text
        
        PFUser.logInWithUsernameInBackground(user.username!, password: (user.password)!) {
            (return_user: PFUser?, error: NSError?) -> Void in
            if return_user != nil {
                // Do stuff after successful login.
            } else {
                // The login failed. Check error to see why.
                user.signUpInBackgroundWithBlock {
                    (succeeded: Bool, error: NSError?) -> Void in
                    if let error = error {
                        _ = error.userInfo["error"] as? NSString
                        self.signIn_alert(self)
                        // Show the errorString somewhere and let the user try again.
                    } else {
                        // Hooray! Let them use the app now.
                        self.performSegueWithIdentifier("tabBarForPassDriver", sender: self)
                    }
                }
            }
        }
    }    

    func signIn_alert(sender: AnyObject) {
        if #available(iOS 8.0, *) {
            let alertController = UIAlertController(title: "Slugging", message:
                "Email in use, please enter correct password.", preferredStyle: UIAlertControllerStyle.Alert)
            alertController.addAction(UIAlertAction(title: "Dismiss", style: UIAlertActionStyle.Default,handler: nil))
            self.presentViewController(alertController, animated: true, completion: nil)
        } else {
            // Fallback on earlier versions
        }
    }
    
    override func viewDidLoad() {
        super.viewDidLoad()
        // Do any additional setup after loading the view, typically from a nib.
    }
    
    override func viewDidAppear(animated: Bool) {
        let currentUser = PFUser.currentUser()
        if currentUser != nil {
            // Do stuff with the user
            self.performSegueWithIdentifier("tabBarForPassDriver", sender: self)
            
        } else {
            // Show the signup or login screen
        }

    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
}
