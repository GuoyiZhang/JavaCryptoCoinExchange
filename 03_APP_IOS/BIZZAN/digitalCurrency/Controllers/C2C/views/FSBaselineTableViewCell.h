//
//  FSBaselineTableViewCell.h
//  FSScrollViewNestTableViewDemo
//
//  Created by huim on 2019/5/23.
//  Copyright © 2019年 fengshun. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface FSBaselineTableViewCell : UITableViewCell
@property (nonatomic,copy)void (^BusinessBlock)(void);

@end
