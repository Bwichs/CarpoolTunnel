//
//  AccountViewController.swift
//  ParseStarterProject-Swift
//
//  Created by Brian Wichers on 10/15/15.
//  Copyright © 2015 Parse. All rights reserved.
//

import UIKit
import Parse


class AccountViewController: UIViewController, UIImagePickerControllerDelegate, UINavigationControllerDelegate {
    @IBOutlet weak var loadImageButton: UIButton!
    
    @IBOutlet weak var saveChangesButton: UIButton!
    @IBOutlet weak var changePasswordButton: UIButton!
    @IBOutlet weak var deleteAccountButton: UIButton!
    @IBOutlet weak var menuButton: UIBarButtonItem!
    @IBOutlet weak var myCar: UIImageView!
    @IBOutlet weak var myName: UITextField!
    @IBOutlet weak var myEmail: UITextField!
    @IBOutlet weak var myNumber: UITextField!
    @IBOutlet weak var myCarType: UITextField!

    let imagePicker = UIImagePickerController()
    
    @IBAction func loadImage(sender: AnyObject) {
        imagePicker.allowsEditing = false
        imagePicker.sourceType = .PhotoLibrary
        
        presentViewController(imagePicker, animated: true, completion: nil)
    }
    
    func notEditingAccount(){
        saveChangesButton.hidden = true
        loadImageButton.hidden = true
        deleteAccountButton.hidden = false
        changePasswordButton.hidden = false
        myName.userInteractionEnabled = false
        myName.borderStyle = UITextBorderStyle.None
        myEmail.userInteractionEnabled = false
        myEmail.borderStyle = UITextBorderStyle.None
        myNumber.userInteractionEnabled = false
        myNumber.borderStyle = UITextBorderStyle.None
        myCarType.userInteractionEnabled = false
        myCarType.borderStyle = UITextBorderStyle.None
    }
    
    func editingAccount(){
        saveChangesButton.hidden = false
        loadImageButton.hidden = false
        deleteAccountButton.hidden = true
        changePasswordButton.hidden = true
        myName.userInteractionEnabled = true
        myName.borderStyle = UITextBorderStyle.RoundedRect
        myNumber.userInteractionEnabled = true
        myNumber.borderStyle = UITextBorderStyle.RoundedRect
        myCarType.userInteractionEnabled = true
        myCarType.borderStyle = UITextBorderStyle.RoundedRect
    }
    
    override func viewDidLoad() {
        super.viewDidLoad()
        notEditingAccount()
        
        imagePicker.delegate = self
        
        let currentId = PFUser.currentUser()?.objectId
        let query = PFUser.query()
        query!.getObjectInBackgroundWithId(currentId!) {
            (myself: PFObject?, error: NSError?) -> Void in
            if error == nil && myself != nil {
                self.myName.text = myself!.objectForKey("name") as? String
                self.myCarType.text = myself!.objectForKey("carType") as? String
                self.myEmail.text = myself!.objectForKey("email") as? String
                self.myNumber.text = myself!.objectForKey("phoneNumber") as? String
                if let carImage = myself!["carpic"] as? PFFile {
                    carImage.getDataInBackgroundWithBlock({
                        (imageData: NSData?, error NSError) -> Void in
                        if (error == nil) {
                            let image = UIImage(data:imageData!)
                            self.myCar.image = image
                        }
                    })
                }
            }
            else {
                print(error)
            }
        }
        
        // Do any additional setup after loading the view.
        if self.revealViewController() != nil {
            menuButton.target = self.revealViewController()
            menuButton.action = "revealToggle:"
            self.view.addGestureRecognizer(self.revealViewController().panGestureRecognizer())
        }
        self.revealViewController().rearViewRevealWidth = 160
    }
    
    func imagePickerController(
        picker: UIImagePickerController,
        didFinishPickingMediaWithInfo info: [String : AnyObject]) {
        if let pickedImage = info[UIImagePickerControllerOriginalImage] as? UIImage {
            myCar.contentMode = .ScaleAspectFit
            myCar.image = pickedImage
        }
        
        dismissViewControllerAnimated(true, completion: nil)
    }
        
    func imagePickerControllerDidCancel(picker: UIImagePickerController) {
        dismissViewControllerAnimated(true, completion: nil)
    }
    
    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    @IBAction func saveChanges(sender: AnyObject) {
        let currentId = PFUser.currentUser()?.objectId
        let query = PFUser.query()
        query!.getObjectInBackgroundWithId(currentId!) {
            (myself: PFObject?, error: NSError?) -> Void in
            if error == nil && myself != nil {
                myself!["name"] = self.myName.text
                myself!["carType"] = self.myCarType.text
                myself!["email"] = self.myEmail.text
                myself!["phoneNumber"] = self.myNumber.text
                
                var carImageFile = "carpic.jpg"
                if (self.myCarType.text != "") {
                    carImageFile = self.myCarType.text! + ".jpg"
                }
                
                
                if let imageData = UIImageJPEGRepresentation(self.myCar.image!, 0.5){
                    let imageFile = PFFile(name: carImageFile, data: imageData)
                    myself!["carpic"] = imageFile
                }
                myself!.saveInBackgroundWithBlock {
                    (success, error) -> Void in
                }
            }
            else {
                print(error)
            }
        }
        notEditingAccount()
    }
    
    @IBAction func editAccount(sender: AnyObject) {
        editingAccount()
    }

    @IBAction func deleteAccount(sender: AnyObject) {
    }
    
    @IBAction func changePassword(sender: AnyObject) {
    }
    
    
    /*
    // MARK: - Navigation

    // In a storyboard-based application, you will often want to do a little preparation before navigation
    override func prepareForSegue(segue: UIStoryboardSegue, sender: AnyObject?) {
        // Get the new view controller using segue.destinationViewController.
        // Pass the selected object to the new view controller.
    }
    */

}
